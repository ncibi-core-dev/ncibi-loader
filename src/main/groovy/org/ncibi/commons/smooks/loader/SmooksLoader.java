package org.ncibi.commons.smooks.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Result;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.xml.sax.SAXException;

public class SmooksLoader extends SmooksDataLoader
{
    /**
     * Constructor. Both files must exist. Creates streams from the files.
     * 
     * @param dataFileStream
     *            The data file to transform.
     * @param configFileStream
     *            The smook configuration file to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws FileNotFoundException
     *             When one of the files (data or config) can't be found.
     */
    public SmooksLoader(final FileInputStream dataFileStream,
            final FileInputStream configFileStream, final Result result)
    {
        super(dataFileStream, configFileStream, result);
    }

    /**
     * Constructor. Takes data and configuration as a file input stream.
     * 
     * @param dataInputStream
     *            The data stream to transform.
     * @param configInputStream
     *            The smooks configuration stream to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     */
    public SmooksLoader(final InputStream dataInputStream,
            final InputStream configInputStream, final Result result)
    {
        super(dataInputStream, configInputStream, result);
    }

    /**
     * Constructor. Take data and configuration as a general input stream.
     * 
     * @param dataFilePath
     *            The data stream to transform.
     * @param configFilePath
     *            The smooks configuration stream to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws java.io.FileNotFoundException
     *             When file is missing
     */
    public SmooksLoader(final String dataFilePath, final String configFilePath,
            final Result result) throws FileNotFoundException
    {
        super(dataFilePath, configFilePath, result);
    }

    /**
     * Loads and transforms the data using smooks. Closes the dataFileStream and
     * smooksConfigStream.
     * 
     * @throws IOException
     * @throws SAXException
     * @throws SmooksException
     */
    @Override
    protected void loadUsingSmooks() throws IOException, SAXException,
            SmooksException
    {
        final Smooks smooks = new Smooks(smooksConfigStream);
        try
        {
            final ExecutionContext executionContext = smooks
                    .createExecutionContext();
            smooks.filterSource(executionContext, dataFileStream, result);
            // smooks.filter(dataFileStream, result, executionContext);
        }
        finally
        {
            loaded = true;
            smooks.close();
        }
    }

}
