package org.ncibi.commons.smooks.loader;

import org.ncibi.commons.exception.LoadException;

/**
 * Interface for all data loaders. Loader implementations must be thread safe
 * and allow for the load() method to be called multiple times.
 * 
 * @param <T>
 *            The type of data items to load.
 * @author gtarcea
 * 
 */
public interface DataLoader<T>
{
    /**
     * Loads a set of data items. If load() is called a second time then it will
     * discard the currently loaded list and redo the load.
     * 
     * @return this
     * @throws LoadException
     */
    public DataLoader<T> load();

    /**
     * Returns the list of data items loaded. If getResults() is called before
     * calling load() then it returns an empty list.
     * 
     * @return The results of the load.
     */
    public T getResults();
}
