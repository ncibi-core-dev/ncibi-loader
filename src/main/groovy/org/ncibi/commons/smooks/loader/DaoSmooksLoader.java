package org.ncibi.commons.smooks.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.transform.Result;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.register.DaoRegister;
import org.xml.sax.SAXException;

/**
 * 
 * @author gtarcea
 */
public class DaoSmooksLoader extends SmooksDataLoader
{
    /**
     *
     */
    // private final

    /**
     * The entity manager to use.
     */
    private final EntityManager em;
    /**
     * 
     */
    private final DaoRegister<DataSourceDao> register;

    /**
     * Constructor.
     * 
     * @param persistenceUnit
     *            Persistence unit to use for JPA.
     * @param dataFileStream
     *            The data file to transform.
     * @param configFileStream
     *            The smook configuration file to use.
     * @param daoRegister
     *            The Dao Register to register for the Persistence unit.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws FileNotFoundException
     *             When one of the files (data or config) can't be found.
     */
    public DaoSmooksLoader(final EntityManager em,
            final FileInputStream dataFileStream,
            final FileInputStream configFileStream,
            DaoRegister<DataSourceDao> daoRegister, final Result result)
    {
        super(dataFileStream, configFileStream, result);
        this.em = em;
        register = daoRegister;
    }

    /**
     * Constructor.
     * 
     * @param persistenceUnit
     *            Persistence unit to use for JPA.
     * @param dataInputStream
     *            The data file to transform.
     * @param configInputStream
     *            The smook configuration file to use.
     * @param daoRegister
     *            The Dao Register to register for the Persistence unit.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @param daoRegister
     *            The Dao Register to register for the Persistence unit.
     */
    public DaoSmooksLoader(final EntityManager em,
            final InputStream dataInputStream,
            final InputStream configInputStream,
            DaoRegister<DataSourceDao> daoRegister, final Result result)
    {
        super(dataInputStream, configInputStream, result);
        this.em = em;
        register = daoRegister;
    }

    /**
     * Constructor.
     * 
     * @param persistenceUnit
     *            Persistence unit to use for JPA.
     * @param dataFilePath
     *            The data file to transform.
     * @param configFilePath
     *            The smook configuration file to use.
     * @param daoRegister
     *            The Dao Register to register for the Persistence unit.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws java.io.FileNotFoundException
     *             When file is missing.
     */
    public DaoSmooksLoader(final EntityManager em, final String dataFilePath,
            final String configFilePath,
            DaoRegister<DataSourceDao> daoRegister, final Result result)
            throws FileNotFoundException
    {
        super(dataFilePath, configFilePath, result);
        this.em = em;
        register = daoRegister;
    }

    /**
     * Loads the data using Smooks to transform it and JPA to store the java
     * beans. Registers a set of Dao's that perform the actual insertions.
     */
    @Override
    protected void loadUsingSmooks() throws IOException, SAXException,
            SmooksException
    {
        final Smooks smooks = new Smooks(smooksConfigStream);
        EntityTransaction tx = null;

        try
        {
            final ExecutionContext executionContext = smooks
                    .createExecutionContext();

            PersistenceUtil.setDAORegister(executionContext, register);

            tx = em.getTransaction();
            tx.begin();
            smooks.filterSource(executionContext, dataFileStream, result);
            tx.commit();
        }
        finally
        {
            smooks.close();
        }
    }
}
