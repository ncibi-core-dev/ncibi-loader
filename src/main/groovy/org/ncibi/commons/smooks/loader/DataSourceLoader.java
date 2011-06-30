package org.ncibi.commons.smooks.loader;

import java.io.InputStream;

import javax.xml.transform.Result;

/**
 * Interface all data source loaders must implement.
 * 
 * @author V. Glenn Tarcea
 *
 */
public interface DataSourceLoader
{
    public void load(final String datafile, final Result result);
    
    public void load(final InputStream inputStream, final Result result);
}
