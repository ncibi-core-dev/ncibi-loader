package org.ncibi.commons.smooks.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.xml.transform.Result;

import org.milyn.scribe.register.DaoRegister;
import org.ncibi.commons.exception.ConstructorCalledError;
import org.ncibi.commons.exception.LoadException;
import org.ncibi.commons.lang.ClassLoaderUtils;
import org.ncibi.commons.smooks.loader.DaoSmooksLoader;
import org.ncibi.commons.smooks.loader.DataSourceDao;
import org.ncibi.commons.smooks.loader.JpaSmooksLoader;
import org.ncibi.commons.smooks.loader.SmooksDataLoader;
import org.ncibi.commons.smooks.loader.SmooksLoader;

/**
 * Utility class for using Smooks.
 * 
 * @author V. Glenn Tarcea
 * 
 */
public final class SmooksUtilities
{

    /**
     * Constructor. Not callable.
     */
    private SmooksUtilities()
    {
        throw new ConstructorCalledError(this.getClass());
    }

    /**
     * Runs smooks using the smooks config file against the given data file.
     * 
     * @param dataFile
     *            The datafile to run smooks against.
     * @param smooksConfigFile
     *            The smooks configuration file to use. Assumed to be a system
     *            resource.
     * @param result
     *            The smooks result.
     */
    public static void runSmooks(final String dataFile,
            final String smooksConfigFile, final Result result)
    {
        try
        {
            final SmooksLoader loader = new SmooksLoader(dataFile,
                    ClassLoaderUtils
                            .getSystemResourceAsFilePath(smooksConfigFile),
                    result);
            loader.load();
        }
        catch (final FileNotFoundException e)
        {
            throw new LoadException("File not found. Config file: "
                    + smooksConfigFile + ", dataFile: " + dataFile, e);
        }
    }

    /**
     * Runs smooks using the smooks config file, using the given data file and
     * persistence unit.
     * 
     * @param persistenceUnit
     *            The JPA persistence unit.
     * @param dataFile
     *            The datafile to run smooks against.
     * @param smooksConfigFile
     *            The smooks configuration file to use. Assumed to be a system
     *            resource.
     * @param result
     *            The smooks result.
     */
    public static void runSmooksJpa(final String persistenceUnit,
            final String dataFile, final String smooksConfigFile,
            final Result result)
    {
        try
        {
            final SmooksDataLoader loader = new JpaSmooksLoader(
                    persistenceUnit, dataFile, ClassLoader.getSystemResource(
                            smooksConfigFile).getFile(), result);
            loader.load();
        }
        catch (final FileNotFoundException e)
        {
            throw new LoadException("File not found. Config file: "
                    + smooksConfigFile + ", dataFile: " + dataFile, e);
        }
    }

    /**
     * Runs smooks using the smooks config file, using the given data file and
     * persistence unit.
     * 
     * @param persistenceUnit
     *            The JPA persistence unit.
     * @param dataFile
     *            The datafile to run smooks against.
     * @param smooksConfigFile
     *            The smooks configuration file to use. Assumed to be a system
     *            resource.
     * @param register
     *            The DAO handlers to register.
     * @param result
     *            The smooks result.
     */
    public static void runSmooksDao(final EntityManager em,
            final String dataFile, final String smooksConfigFile,
            final DaoRegister<DataSourceDao> register, final Result result)
    {
        try
        {
            final SmooksDataLoader loader = new DaoSmooksLoader(em, dataFile,
                    ClassLoader.getSystemResource(smooksConfigFile).getFile(),
                    register, result);
            loader.load();
        }
        catch (final FileNotFoundException e)
        {
            throw new LoadException("File not found. Config file: "
                    + smooksConfigFile + ", dataFile: " + dataFile, e);
        }
    }

    /**
     * Runs smooks using the smooks config file, using the given data file and
     * persistence unit.
     * 
     * @param persistenceUnit
     *            The JPA persistence unit.
     * @param dataStream
     *            The data stream to run smooks against.
     * @param smooksConfigFile
     *            The smooks configuration file to use. Assumed to be a system
     *            resource.
     * @param result
     *            The smooks result.
     */
    public static void runSmooksJpa(final String persistenceUnit,
            final InputStream dataStream, final String smooksConfigFile,
            final Result result)
    {
        try
        {
            final SmooksDataLoader loader = new JpaSmooksLoader(
                    persistenceUnit, dataStream, ClassLoader.getSystemResource(
                            smooksConfigFile).openStream(), result);
            loader.load();
        }
        catch (final Exception e)
        {
            throw new LoadException("Unable to process data: "
                    + smooksConfigFile, e);
        }
    }

    /**
     * Runs smooks using the smooks config file, using the given data file and
     * persistence unit.
     * 
     * @param persistenceUnit
     *            The JPA persistence unit.
     * @param dataStream
     *            The data stream to run smooks against.
     * @param smooksConfigFile
     *            The smooks configuration file to use. Assumed to be a system
     *            resource.
     * @param register
     *            The DAO handlers to register.
     * @param result
     *            The smooks result.
     */
    public static void runSmooksDao(final EntityManager em,
            final InputStream dataStream, final String smooksConfigFile,
            final DaoRegister<DataSourceDao> register, final Result result)
    {
        try
        {
            final SmooksDataLoader loader = new DaoSmooksLoader(em, dataStream,
                    ClassLoader.getSystemResource(smooksConfigFile)
                            .openStream(), register, result);
            loader.load();
        }
        catch (final Exception e)
        {
            throw new LoadException("Unable to process data: "
                    + smooksConfigFile, e);
        }
    }
}
