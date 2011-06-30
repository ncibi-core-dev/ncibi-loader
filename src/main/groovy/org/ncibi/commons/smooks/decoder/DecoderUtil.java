package org.ncibi.commons.smooks.decoder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.ncibi.commons.exception.ConstructorCalledError;
import org.ncibi.commons.lang.IterableUtil;

import com.google.common.base.Splitter;

public final class DecoderUtil
{
    public static final String DEFAULT_TYPE_VALUE_SEPARATOR = ":";
    public static final String DEFAULT_LIST_SEPARATOR = ",";
    
    private DecoderUtil()
    {
        throw new ConstructorCalledError(this.getClass());
    }
    
    /**
     * Given an entry and a separator creates a TypeValue. If the entry is invalid
     * (either type or value are null) then returns null.
     * 
     * @param entry A type/value string separated by separator, eg "pmid:1234".
     * @param separator The separator separating the type and value. If null then uses DEFAULT_TYPE_VALUE_SEPARATOR.
     * @return A TypeValue, or null if the entry is determined to be invalid.
     */
    public static TypeValue typeValueFrom(final String entry, final String separator)
    {   
        if (entry == null)
        {
            return null;
        }
        
        final String s = separator == null ? DEFAULT_TYPE_VALUE_SEPARATOR : separator;
        final Splitter splitter = splitterOn(s);
        final Iterable<String> iterable = splitter.omitEmptyStrings().trimResults().split(entry);
        final String type = IterableUtil.getAt(iterable, 0, null);
        final String value = IterableUtil.getAt(iterable, 1, null);
        
        if (type == null || value == null)
        {
            return null;
        }
        
        return new TypeValue(type, value);
    }
    
    /**
     * 
     * @param entryList
     * @param defaultValue
     * @param listSeparator
     * @param valueSeparator
     * @return
     */
    public static List<TypeValue> typeValueListFrom(final String entryList, final TypeValue defaultValue, final String listSeparator, final String valueSeparator)
    {
        if (entryList == null)
        {
            return Collections.emptyList();
        }
        
        final String lseparator = listSeparator == null ? DEFAULT_LIST_SEPARATOR : listSeparator;
        final List<TypeValue> typeValueList = new LinkedList<TypeValue>();
        final Splitter splitter = splitterOn(lseparator);
        
        for (final String entry : splitter.omitEmptyStrings().trimResults().split(entryList))
        {
            final TypeValue tvalue = typeValueFrom(entry, valueSeparator);
            if (tvalue == null)
            {
                if (defaultValue != null)
                {
                    typeValueList.add(defaultValue);
                }
            }
            else
            {
                typeValueList.add(tvalue);
            }
        }
        
        return typeValueList;
    }
    
    /**
     * 
     * @param separator
     * @return
     */
    public static Splitter splitterOn(String separator)
    {
        if (separator == null)
        {
            throw new IllegalStateException("No separator specified.");
        }
        
        if (separator.startsWith("\\r"))
        {
            Pattern p = Pattern.compile(separator.substring(2));
            return Splitter.on(p);
        }
        else
        {
            return Splitter.on(separator);
        }
    }
    
    /**
     * 
     * @param separator
     * @param defaultSeparator
     * @return
     */
    public static Splitter splitterOn(String separator, String defaultSeparator)
    {
        if (separator == null)
        {   
            return splitterOn(defaultSeparator);
        }
        else
        {
            return splitterOn(separator);
        }
    }
}
