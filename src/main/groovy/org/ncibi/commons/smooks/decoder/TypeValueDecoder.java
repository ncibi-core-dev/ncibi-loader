package org.ncibi.commons.smooks.decoder;

import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Parses a Type-Value pair into a TypeValue. A TypeValue pair has two values separated by a separtor character. For example "pmid:1234".
 * 
 * @author gtarcea
 *
 */
public final class TypeValueDecoder extends AbstractConfigurable implements DataDecoder
{   
    @Override
    public Object decode(String entry) throws DataDecodeException
    {
        final String separator = config.getProperty("separator");
        TypeValue tvalue = DecoderUtil.typeValueFrom(entry, separator);
        
        if (tvalue == null)
        {
            final String defaultValue = config.getProperty("defaultValue");
            if (defaultValue != null)
            {
                tvalue = DecoderUtil.typeValueFrom(defaultValue, separator);
            }
            else
            {
                throw new DataDecodeException("Entry is not a TypeValue pair that can be split using " + separator + ", entry: " + entry);
            }
        }
        
        return tvalue;
    }
}
