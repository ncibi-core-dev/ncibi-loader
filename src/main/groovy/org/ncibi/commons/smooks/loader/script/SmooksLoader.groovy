package org.ncibi.commons.smooks.loader.script;

import java.io.File;
import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory 
import javax.persistence.Persistence

import org.milyn.scribe.register.DaoRegister;
import org.ncibi.commons.smooks.loader.DaoDataSourceLoader 
import org.ncibi.commons.smooks.loader.DataSourceLoader 
import java.util.zip.GZIPInputStream

import org.ncibi.commons.config.LoaderConfiguration
import org.ncibi.commons.io.FileUtilities
import groovy.sql.Sql;
import org.milyn.scribe.register.MapDaoRegister
import org.ncibi.commons.smooks.dao.CachingDao
import org.ncibi.commons.lang.ClassLoaderUtils;

/**
 * Base extendible class for creating a Smooks loader script.
 * Scripts that will use Smooks to do their loading and writing
 * to the database can extend and use this script.
 * 
 * @author gtarcea
 *
 */
final class SmooksLoader
{
    private final DaoRegister<Object> register
    private final EntityManager em
    private final LoaderConfiguration loaderConfig
    private final String loader
    private final String loaderDir
    private final Sql db

    private SmooksLoader(String loader, closure)
    {
        this.loader = loader
        this.loaderConfig = new LoaderConfiguration(loader)
        final String persistenceUnit = loaderConfig.getProperty("persistence-unit")
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit)
        
        this.loaderDir = ClassLoaderUtils.getSystemResourceAsFilePath("loaders/${loader}") - '/'
        this.em = emf.createEntityManager()
        this.db = new Sql(em.getDelegate().connection())
        def dao = new CachingDao(em, closure)
        this.register = MapDaoRegister.builder().put("ncibi", dao).build()
    }
    
    public static SmooksLoader of(String loader, closure)
    {
        return new SmooksLoader(loader, closure)
    }

    protected void initializeDatabase()
    {
        if (FileUtilities.fileExists("${loaderDir}/SQL/init.sql"))
        {
            runLoaderSqlScript("init.sql")
        }
    }
    
    public void finishLoad()
    {
        if (FileUtilities.fileExists("${loaderDir}/SQL/finish.sql"))
        {
            runLoaderSqlScript("finish.sql")
        }
    }
    
    public final InputStream openAsInputStream(File f)
    {
        def inputStream = f.newInputStream()
        return (f =~ /\.gz$/) ? new GZIPInputStream(inputStream) : inputStream
    }
    
    protected final void loadDataSource(String line)
    {
        /*
         * Line format:
         * Datasource=smooks-file=directory=match
         */
        if (line[0] == '#') { return } // Skip comments
        def pieces = line.tokenize("=")
        println("Loading datasource ${pieces[0]}")
        def smooksFile = pieces[1]
        File dsdir = new File(pieces[2])
        def matchstr = pieces[3]
        println("  Smooks: ${smooksFile}")
        println("  Directory: ${dsdir}")
        println("  Matchstr: ${matchstr}")
        
        if (matchstr[0] == "*")
        {
            matchstr = "." + matchstr
        }
        
        DataSourceLoader loader = new DaoDataSourceLoader(em, register, "loaders/${loader}/smooks/${smooksFile}")
        dsdir.eachFileMatch(~matchstr) { file ->
            println("  processing ${file}...")
            InputStream inputStream = openAsInputStream(file)
            loader.load(inputStream, null)
        }
    }
    
    public static def runSqlScript(dbConnection, script)
    {
        String sql = new File(script).text
        dbConnection.execute(sql)
    }

    public final def runLoaderSqlScript(script)
    {
        String sql = new File(loaderSqlScript(script)).text
        db.execute(sql)
    }

    protected final def loaderSqlScript(script)
    {
        String scriptPath = ClassLoaderUtils.getSystemResourceAsFilePath("loaders/${loader}/SQL/${script}")
        return scriptPath
    }
    
    public void run(boolean initializeDb)
    {
        if (initializeDb)
        {
            initializeDatabase()
        }
        
        File ds = new File(ClassLoaderUtils.getSystemResourceAsFilePath("loaders/${loader}/datasources.txt"))
        ds.eachLine { line ->
            loadDataSource(line)
        }
        finishLoad()
    }
}
