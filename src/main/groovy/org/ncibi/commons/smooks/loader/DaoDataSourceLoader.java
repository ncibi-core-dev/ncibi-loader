package org.ncibi.commons.smooks.loader;

import java.io.InputStream;
import javax.xml.transform.Result;
import org.milyn.scribe.register.DaoRegister;
import javax.persistence.EntityManager;
import org.ncibi.commons.smooks.util.SmooksUtilities;

/**
 *
 * @author gtarcea
 */
public class DaoDataSourceLoader implements DataSourceLoader
{
    /**
     */
    private final EntityManager em;

    /**
     */
    private final String smooksConfigFile;

    /**
     */
    private final DaoRegister<DataSourceDao> register;

    /**
     */
    public DaoDataSourceLoader(final EntityManager em,
        DaoRegister<DataSourceDao> register,
        final String smooksConfigFile)
    {
        this.em = em;
        this.smooksConfigFile = smooksConfigFile;
        this.register = register;
    }

    /**
     */
    public void load(final String dataFilePath, final Result result)
    {
        SmooksUtilities.runSmooksDao(this.em, dataFilePath,
            this.smooksConfigFile, this.register, result);
    }

    /**
     */
    public void load(final InputStream inputStream, Result result)
    {
        SmooksUtilities.runSmooksDao(this.em, inputStream,
            this.smooksConfigFile, this.register, result);
    }
}
