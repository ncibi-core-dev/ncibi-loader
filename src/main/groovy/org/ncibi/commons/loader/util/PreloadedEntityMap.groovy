package org.ncibi.commons.loader.util;

class PreloadedEntityMap
{
    private final def lookupMap = [:]
    
    public PreloadedEntityMap(String sql, String classKey, Class cls, session)
    {
        def entityList = session.createSQLQuery(sql).addEntity(cls).list()
        entityList.each { entity ->
            lookupMap[entity."${classKey}"] = entity
        }
    }
    
    public def getEntityByKey(key)
    {
        lookupMap[key]
    }
    
    public def getLookupMap()
    {
        return lookupMap;
    }
}
