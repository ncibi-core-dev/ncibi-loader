package org.ncibi.commons.smooks.loader;

import java.io.InputStream;

import javax.xml.transform.Result;

import org.ncibi.commons.smooks.util.SmooksUtilities;

/**
 * Loads a data source using smooks and JPA.
 * 
 * @author V. Glenn Tarcea
 * 
 */
public class JpaDataSourceLoader implements DataSourceLoader
{
    /**
     * The persistence unit to use.
     */
    private final String persistenceUnit;

    /**
     * The smooks config file to use.
     */
    private final String smooksConfigFile;

    /**
     * Constructor.
     * 
     * @param persistenceUnit
     *            The persistence unit to use.
     * @param smooksConfigFile
     *            The Smooks config file to use.
     */
    public JpaDataSourceLoader(final String persistenceUnit,
            final String smooksConfigFile)
    {
        this.persistenceUnit = persistenceUnit;
        this.smooksConfigFile = smooksConfigFile;
    }

    /**
     * Loads the given data file applying the smooks transformations to it.
     * 
     * @param dataFilePath
     *            The full path for the data file.
     * @param result
     *            The result of the load.
     */
    public void load(final String dataFilePath, final Result result)
    {
        SmooksUtilities.runSmooksJpa(persistenceUnit, dataFilePath,
                smooksConfigFile, result);
    }

    /**
     * Loads the given data stream applying the smooks transformations to it.
     * 
     * @param inputStream
     *            The inputStream to load
     * @param result
     *            The result of the load
     */
    public void load(final InputStream inputStream, Result result)
    {
        SmooksUtilities.runSmooksJpa(persistenceUnit, inputStream,
                smooksConfigFile, result);
    }
}
