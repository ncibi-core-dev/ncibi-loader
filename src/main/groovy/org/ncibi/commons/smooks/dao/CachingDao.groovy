/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ncibi.commons.smooks.dao

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.milyn.scribe.annotation.Dao;
import org.milyn.scribe.annotation.Insert;
import org.milyn.scribe.annotation.Flush;
import java.sql.Connection;
import org.ncibi.commons.smooks.loader.DataSourceDao


/**
 * Caches writes to the database so that inserts can be done in groups.
 * Must be used as a part of the Smook Dao.
 * 
 * @author gtarcea
 */
@Dao
public class CachingDao implements DataSourceDao
{
    private final EntityManager em
    private def cache = []
    private final int cacheSize
    private final def persistObject
    private def onFlush
    boolean logWriteCount = true
    
    private static int total = 0
    
    
    public CachingDao(int cacheSize, EntityManager em, Closure persistObject)
    {
        this.cacheSize = cacheSize
        this.em = em
        this.persistObject = persistObject
    }
    
    public CachingDao(EntityManager em, Closure persistObject)
    {
        this.cacheSize = 500
        this.em = em
        this.persistObject = persistObject
    }
    
    public def setOnFlush(onFlush)
    {
        this.onFlush = onFlush
    }
    
    @Insert
    public void insertObject(Object obj)
    {
        if (cache.size() < cacheSize)
        {
            cache.add(obj)
        }
        else
        {
            writeCache(false)
            cache.add(obj)
        }
    }
    
    private void writeCache(boolean flushing)
    {
        if (logWriteCount)
        {
            println("Writing ${cache.size()} objects/written so far: ${total}")
        }
        
        total += cache.size()
        long now =  System.currentTimeMillis()
        def session = em.getDelegate()
        for (Object o in cache)
        {
            persistObject(session, o, flushing)
        }
        session.flush()
        session.clear()
        long after =  System.currentTimeMillis()
        if (onFlush)
        {
            onFlush()
        }
        cache = []
        
        if (logWriteCount)
        {
            println("done (${(after - now)/1000} seconds)....")
        }
    }
    
    @Flush
    public void flushCache()
    {
        writeCache(true)
    }
}

