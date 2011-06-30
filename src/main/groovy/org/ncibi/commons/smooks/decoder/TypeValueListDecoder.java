package org.ncibi.commons.smooks.decoder;

import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Parses a line of TypeValue pairs turning it into a list of TypeValue.
 * 
 * @author gtarcea
 * 
 */
public class TypeValueListDecoder extends AbstractConfigurable implements DataDecoder
{

    @Override
    public Object decode(final String entry) throws DataDecodeException
    {
        final String valueSeparator = config.getProperty("valueSeparator");
        final String listSeparator = config.getProperty("listSeparator");
        final String defaultEntry = config.getProperty("defaultValue");
        final TypeValue defaultValue = DecoderUtil.typeValueFrom(defaultEntry, valueSeparator);

        return DecoderUtil.typeValueListFrom(entry, defaultValue, listSeparator, valueSeparator);
    }

}
