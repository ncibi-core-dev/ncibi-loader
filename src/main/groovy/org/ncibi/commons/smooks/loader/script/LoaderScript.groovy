package org.ncibi.commons.smooks.loader.script;

import org.ncibi.commons.config.LoaderConfiguration
import org.ncibi.commons.io.FileUtilities
import org.ncibi.commons.lang.ClassLoaderUtils;

class LoaderScript
{
    final String loader
    final LoaderConfiguration config
    final String loaderDir
    def runScript
    
    public LoaderScript(String loader, script)
    {
        this.loader = loader
        this.config = new LoaderConfiguration(loader)
        this.loaderDir = LoaderUtil. getLoaderDir(loader) - '/'
        this.runScript = script
    }

    protected void initializeDatabase()
    {
        // Can be overridden.
        if (FileUtilities.fileExists("$loaderDir/SQL/init.sql"))
        {
            println("\nRunning SQL/init.sql...")
            LoaderUtil.runSqlScript(LoaderUtil.sqlInstance(config), 
                    LoaderUtil.getLoaderResource(loader, "SQL/init.sql").getPath())
            println("Done.")
        }
    }
    
    protected void finishLoad()
    {
        // Can be overridden.
        if (FileUtilities.fileExists("$loaderDir/SQL/finish.sql"))
        {
            println("\nRunning SQL/finish.sql...")
            LoaderUtil.runSqlScript(LoaderUtil.sqlInstance(config), 
                    LoaderUtil.getLoaderResource(loader, "SQL/finish.sql").getPath())
            println("Done.")
        }
    }
    
    protected void initializeLoad()
    {
        // Can be overridden.
    }
    
    public void runLoad(boolean initializeDb)
    {
        if (initializeDb)
        {
            initializeDatabase()
        }
        runScript(loader, loaderDir, config)
        finishLoad()
    }
}
