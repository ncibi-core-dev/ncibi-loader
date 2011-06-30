package org.ncibi.commons.smooks.decoder;

import java.util.LinkedList;
import java.util.List;

import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

import com.google.common.base.Splitter;

/**
 * A decoder that splits an entry into a list of strings. The separator for the entry is defaults to
 * ',' but can be configured by setting the "separator" parameter.
 * 
 * @author gtarcea
 * 
 */
public class CSVDecoder extends AbstractConfigurable implements DataDecoder
{
    @Override
    public Object decode(final String line) throws DataDecodeException
    {
        final List<String> entryList = new LinkedList<String>();
        final String separator = config
                .getProperty("separator", DecoderUtil.DEFAULT_LIST_SEPARATOR);
        final Splitter splitter = DecoderUtil.splitterOn(separator);
        
        for (final String entry : splitter.omitEmptyStrings().trimResults().split(line))
        {
            entryList.add(entry);
        }
        return entryList;
    }
}
