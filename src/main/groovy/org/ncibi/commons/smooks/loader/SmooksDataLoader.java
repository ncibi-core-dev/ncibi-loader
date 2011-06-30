package org.ncibi.commons.smooks.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.milyn.SmooksException;
import org.ncibi.commons.exception.LoadException;
import org.xml.sax.SAXException;

/**
 * This is a data loader that loads a data file using Smooks. This class does
 * not implement the DataLoader interface because Smooks allows a number of
 * different Result types to be specified, and only a JavaResult would allow the
 * List<T> getData() to be used.
 * 
 * @author gtarcea
 * 
 */
public abstract class SmooksDataLoader implements DataLoader<Result>
{
    protected abstract void loadUsingSmooks() throws IOException, SAXException,
            SmooksException;

    /**
     * Have we already loaded the pipeline? Pipelines cannot be loaded more than
     * once.
     */
    protected boolean loaded = false;

    /**
     * The smooks result that is populated after running smooks.
     */
    protected final Result result;

    /**
     * The smooks configuration stream containing the smooks transformations.
     */
    protected final InputStream smooksConfigStream;

    /**
     * The data stream to be transformed.
     */
    protected final StreamSource dataFileStream;

    /**
     * Constructor. Both files must exist. Creates streams from the files.
     * 
     * @param dataFilePath
     *            The data file to transform.
     * @param configFilePath
     *            The smook configuration file to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws FileNotFoundException
     *             When one of the files (data or config) can't be found.
     */
    public SmooksDataLoader(final String dataFilePath,
            final String configFilePath, final Result result)
            throws FileNotFoundException
    {
        this(new FileInputStream(dataFilePath), new FileInputStream(
                configFilePath), result);
    }

    /**
     * Constructor. Takes data and configuration as a file input stream.
     * 
     * @param dataFileStream
     *            The data stream to transform.
     * @param configFileStream
     *            The smooks configuration stream to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     */
    public SmooksDataLoader(final FileInputStream dataFileStream,
            final FileInputStream configFileStream, final Result result)
    {
        this.result = result;
        smooksConfigStream = new AutoCloseInputStream(configFileStream);
        this.dataFileStream = new StreamSource(new AutoCloseInputStream(
                dataFileStream));
    }

    /**
     * Constructor. Take data and configuration as a general input stream.
     * 
     * @param dataInputStream
     *            The data stream to transform.
     * @param configInputStream
     *            The smooks configuration stream to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     */
    public SmooksDataLoader(final InputStream dataInputStream,
            final InputStream configInputStream, final Result result)
    {
        this.result = result;
        smooksConfigStream = new AutoCloseInputStream(configInputStream);
        dataFileStream = new StreamSource(new AutoCloseInputStream(
                dataInputStream));
    }

    /**
     * Loads and transforms the data. If any exception occurs then it throws a
     * LoadException. Closes all data sources. This method cannot be called
     * again.
     * 
     * @return this
     * @throws LoadException
     *             When a problem loading or transforming the data occurs.
     * @throws IllegalStateException
     *             If load() is called more than once.
     */
    public SmooksDataLoader load()
    {
        if (loaded) { throw new IllegalStateException("Cannot call load twice"); }
        try
        {
            loadUsingSmooks();
        }
        catch (final Exception e)
        {
            throw new LoadException("Error loading input stream using Smooks.",
                    e);
        }

        return this;
    }

    /**
     * Returns the result of the transformation.
     * 
     * @return The transformation result.
     */
    public Result getResults()
    {
        return result;
    }
}
