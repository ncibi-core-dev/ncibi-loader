package org.ncibi.commons.smooks.loader.script;

import java.io.File;
import org.ncibi.commons.io.FileUtilities;
import org.ncibi.commons.lang.ClassLoaderUtils;
import java.util.Date;
import java.text.DateFormat;

/**
 * Framework for running loader scripts. Runs loader.groovy files under the
 * loader directory in the system resource path.
 * 
 * @author gtarcea
 *
 */
class LoaderRunner
{   
    /**
     * Runs a specific loader by running the loader.groovy file under the loader directory. 
     *  If the loader name includes a ':false' then the 'initDb' flag is set to false and 
     *  passed into the loader script. Otherwise this flag is set to true. The name of the 
     *  loader, in the 'loader' variable, is also passed to the loader.
     *  
     * @param loader The name of the loader, with an option :false attached to the name.
     * @param shell The shell to load the loader.groovy or smooks.groovy file in.
     */
    private static def runLoader(loader, shell)
    {
        println("\nRunning loader ${loader}")
        def initDb = true
        def colon = loader.indexOf(':')
        def loaderName = loader
        if (colon != -1)
        {
            def flag = loader[colon+1..<loader.size()-1]
            if (flag.toLowerCase() == "false")
            {
                initDb = false
            }
            loaderName = loader[0..<colon]
        }
        
        def dir = ClassLoaderUtils.getSystemResourceAsFilePath("loaders/${loaderName}")

        if (FileUtilities.fileExists("${dir}/smooks.groovy"))
        {
            def script = new File("${dir}/smooks.groovy").text
            def persister = shell.evaluate(script)
            SmooksLoader.of(loaderName, persister).run(initDb)
        }
        else if (FileUtilities.fileExists("${dir}/loader.groovy"))
        {
            def script = new File("${dir}/loader.groovy").text
            shell.setVariable("loader", loaderName)
            shell.setVariable("initDb", initDb)
            shell.evaluate(script)
        }
    }
    
    /**
     * Runs all the loaders, treating init and finish as special.
     * 
     * @param shell The groovy shell to run the loader.groovy or smooks.groovy file in.
     */
    private static def runAllLoaders(shell, loadersRun)
    {
        File loadersDir = new File(ClassLoaderUtils.getSystemResourceAsFilePath("loaders"))
        
        if (FileUtilities.fileExists("${loadersDir}/init"))
        {
            runLoader("init", shell)
            loadersRun << "init"
        }
        
        loadersDir.eachDir { loader -> 
            if (loader.name == "init" || loader.name == "finish")
            {
                /*
                 * Skip these two loader directories. They are handled specially.
                 */
                return
            }
            runLoader(loader.name, shell) 
            loadersRun << loader.name
        }
        
        if (FileUtilities.fileExists("${loadersDir}/finish"))
        {
            runLoader("finish", shell)
            loadersRun << "finish"
        }
    }
    
    /**
     * Runs the loaders. If list is null then runs all loaders. If all loaders
     * are run, then 'init', and 'finish' are handled specially. If there is an init
     * directory that step is run first. If there is a finish directory that step
     * is run last. Other than those two special names, all the others are run in
     * alpha number sorted order.
     * 
     * If the list of loaders is non-null then it runs the loaders in the order
     * given and 'init' and 'finish' are not treated as special. Any name can have
     * a ':false' attached to it. Adding this will cause the loader to not automatically
     * run the init.sql script for that loader step (if it exists). The finish.sql script
     * (if it exists) is still run. 
     * 
     * @param loaders List of loaders to run.
     */
    public static void runLoaders(String[] loaders)
    {
        def shell = new GroovyShell()
        
        def loadersRun = []
        
        Date startTime = new Date();
        
        if (loaders)
        {
            // Run listed loaders.
            loaders.each { 
                loader -> runLoader(loader, shell) 
                loadersRun << loader
            }
        }
        else
        {
            /*
             * First check if there is a configured order to run loader, and if so run in
             * that order. Otherwise run all loaders.
             */
            def loaderRunPath = ClassLoaderUtils.getSystemResourceAsFilePath("loaders.run")
            if (loaderRunPath)
            {
                String loadersLine = new File(loaderRunPath).text
                loadersLine.split(' ').each { loader ->
                    runLoader(loader.trim(), shell) 
                    loadersRun << loader
                }
            }
            else
            {
                runAllLoaders(shell, loadersRun)
            }
        }
        
        Date endTime = new Date();
        
        if (loadersRun)
        {
            println("\nLoaders run: " + loadersRun.join(", ") + ".")
        }
        
        String timeStr = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(startTime);
        println("\nStarted load at: ${timeStr}");
        
        timeStr = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(endTime);
        println("Finished load at: ${timeStr}");
    }
}
