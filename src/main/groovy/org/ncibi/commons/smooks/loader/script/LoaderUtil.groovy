package org.ncibi.commons.smooks.loader.script;

import groovy.sql.Sql;
import org.ncibi.commons.lang.ClassLoaderUtils;

class LoaderUtil
{
    static def getLoaderResource(loader, resource)
    {
        return ClassLoaderUtils.getSystemResourceAsUri("loaders/${loader}/${resource}")
    }
    
    static def getLoaderDir(loader)
    {
        return ClassLoaderUtils.getSystemResourceAsFilePath("loaders/${loader}")
    }

    static def runSqlScript(db, script)
    {
        def s = new File(script).text
        db.execute(s)
    }

    static def sqlInstance(loaderConfig)
    {
        Sql.newInstance(loaderConfig.getProperty("db.url"),
                        loaderConfig.getProperty("user.name"),
                        loaderConfig.getProperty("user.password"),
                        loaderConfig.getProperty("db.driver"))
    }
    
    public static String bcpConnectString(config)
    {
        def user = config.getProperty("user.name")
        def password = config.getProperty("user.password")
        def dbServer = config.getProperty("db.server")
        return "-S${dbServer} -U${user} -P${password}"
    }
}
