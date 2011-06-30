package org.ncibi.commons.smooks.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.xml.transform.Result;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.persistence.util.PersistenceUtil;
import org.milyn.scribe.adapter.jpa.EntityManagerRegister;
import org.xml.sax.SAXException;

/**
 * Smooks loader that uses JPA to persist java beans.
 * 
 * @author gtarcea
 */
public class JpaSmooksLoader extends SmooksDataLoader
{
    /**
     * The persistence unit to use for JPA.
     */
    private final String persistenceUnit;

    /**
     * Constructor.
     * 
     * @param persistenceUnit
     *            Persistence unit to use for JPA.
     * @param dataFileStream
     *            The data file to transform.
     * @param configFileStream
     *            The smook configuration file to use.
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws FileNotFoundException
     *             When one of the files (data or config) can't be found.
     */
    public JpaSmooksLoader(final String persistenceUnit,
            final FileInputStream dataFileStream,
            final FileInputStream configFileStream, final Result result)
    {
        super(dataFileStream, configFileStream, result);
        this.persistenceUnit = persistenceUnit;
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
     * @param result
     *            The smooks Result type to store transformation results into.
     */
    public JpaSmooksLoader(final String persistenceUnit,
            final InputStream dataInputStream,
            final InputStream configInputStream, final Result result)
    {
        super(dataInputStream, configInputStream, result);
        this.persistenceUnit = persistenceUnit;
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
     * @param result
     *            The smooks Result type to store transformation results into.
     * @throws java.io.FileNotFoundException
     *             When file is missing.
     */
    public JpaSmooksLoader(final String persistenceUnit,
            final String dataFilePath, final String configFilePath,
            final Result result) throws FileNotFoundException
    {
        super(dataFilePath, configFilePath, result);
        this.persistenceUnit = persistenceUnit;
    }

    /**
     * Loads the data using Smooks to transform it and JPA to store the java
     * beans.
     */
    @Override
    protected void loadUsingSmooks() throws IOException, SAXException,
            SmooksException
    {
        final Smooks smooks = new Smooks(smooksConfigStream);
        final EntityManagerFactory emf = Persistence
                .createEntityManagerFactory(persistenceUnit);
        final EntityManager em = emf.createEntityManager();

        try
        {
            final ExecutionContext executionContext = smooks
                    .createExecutionContext();
            PersistenceUtil.setDAORegister(executionContext,
                    new EntityManagerRegister(em));
            final EntityTransaction tx = em.getTransaction();
            tx.begin();
            smooks.filterSource(executionContext, dataFileStream, result);
            tx.commit();
        }
        finally
        {
            smooks.close();
            em.close();
            emf.close();
        }
    }
}
