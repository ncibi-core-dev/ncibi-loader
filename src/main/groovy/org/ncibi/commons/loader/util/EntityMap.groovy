package org.ncibi.commons.loader.util;

class EntityMap
{
    private def entityMap = [:]
    private final String sqlToUse
    private final Class classToUse
    private final int maxCacheSize = 5000
    private int cacheSize = 0
    private final def session
    
    public EntityMap(String sql, Class classToUse, session)
    {
        this.classToUse = classToUse
        this.sqlToUse = sql
        this.session = session
    }
    
    public EntityMap(String sql, Class classToUse, int maxCacheSize, session)
    {
        this(sql, classToUse, session)
        this.maxCacheSize = maxCacheSize
    }
    
    private def createKey(key)
    {
        if (key instanceof Integer) { return key }
        else { return key.trim().replaceAll("'", "''") }
    }
    
    private def createSql(key)
    {
        if (key instanceof Integer) { return "${sqlToUse}${key}" }
        else { return "${sqlToUse}'${key}'" }
    }
    
    public def getEntityByKey(key)
    {
        def tkey = createKey(key)
        def entity = entityMap[tkey]
        
        if (! entity)
        {
            if (cacheSize > maxCacheSize) 
            { 
                entityMap = [:] 
                cacheSize = 0
            }
            
            def sql = createSql(tkey)
            
            def e = session().createSQLQuery(sql).addEntity(classToUse).uniqueResult()
            if (e)
            {
                entityMap[tkey] = e
                cacheSize++
                entity = e
            }
        }
        entity
    }
    
    public def addEntity(entity)
    {
        session().saveOrUpdate(entity)
        session().flush()
    }
    
    public def getEntityMap()
    {
        return entityMap
    }
    
    public def clearMap()
    {
        entityMap = [:]
        cacheSize = 0
    }
}
