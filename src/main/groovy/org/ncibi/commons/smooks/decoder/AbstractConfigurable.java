package org.ncibi.commons.smooks.decoder;

import java.util.Properties;

import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.config.Configurable;

/**
 * A base class that a Configurable. Since most classes implementing the Configurable interface will
 * need to get the config Properties and save it as a class variable this class saves the boiler
 * plate code.
 * 
 * @author gtarcea
 * 
 */
public class AbstractConfigurable implements Configurable
{
    /**
     * Accessible by classes extending this class. Provides access to the configuration for the
     * decoder.
     */
    protected Properties config;

    @Override
    public void setConfiguration(final Properties config) throws SmooksConfigurationException
    {
        this.config = config;
    }

}
