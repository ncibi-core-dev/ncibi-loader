package org.ncibi.commons.smooks.decoder;

import org.apache.commons.lang.math.NumberUtils;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;

/**
 * Extracts an integer from a tagged value. A tagged value is an entry separated by a separator. The
 * entry is of a format like: "pmid:1234". The integer is to the right of the tag (after the
 * separator - in this example the ':' is the separator.
 * 
 * @author gtarcea
 * 
 */
public class TaggedIntegerDecoder extends AbstractConfigurable implements DataDecoder
{

    /**
     * Given a TypeValue attempts to extract an integer from the value portion. If a default value
     * is given the default value is returned if the TypeValue doesn't contain a parseable integer.
     * The routine is slightly complicated by the fact that it needs to check multiple cases: is
     * tvalue null? is defaultValue also null? is only one of these two null? - Checking for these
     * cases complicates the routine a bit.
     * 
     * @param tvalue
     *            The tvalue to use the value from to parse for an integer.
     * @param defaultValue
     *            If specified, the default value to return when an integer cannot be parsed out of
     *            tvalue.
     * @return Either the integer found from parsing the value in tvalue, or the defaultValue if
     *         tvalue doesn't contain a valid integer value to parse.
     * @throws DataDecodeException
     *             when no value can be found to return.
     */
    private Integer getIntegerValueFor(final TypeValue tvalue, final Integer defaultValue)
    {
        if (tvalue != null)
        {
            /*
             * tvalue isn't null so we can try and parse the value portion.
             */
            try
            {
                final Integer value = Integer.parseInt(tvalue.getValue());
                return value;
            }
            catch (final NumberFormatException e)
            {
                /*
                 * The value wasn't a parseable integer. Return the default value if it exists,
                 * otherwise throw an exception.
                 */
                if (defaultValue != null)
                {
                    return defaultValue;
                }
                else
                {
                    throw new DataDecodeException("Entry isn't a number: " + tvalue.getValue());
                }
            }
        }
        else
        {
            /*
             * tvalue is invalid (null) - return defaultValue if one is specified otherwise throw an
             * exception.
             */
            if (defaultValue != null)
            {
                return defaultValue;
            }
            else
            {
                throw new DataDecodeException("Bad entry with no default value specified.");
            }
        }
    }

    /**
     * Decodes the String in entry by splitting it at separator and treating the 2nd half as an
     * integer. If an integer can't be created then return the configured badvalue, or if badvalue
     * not specified then throw an exception.
     */
    @Override
    public Object decode(final String entry) throws DataDecodeException
    {
        final String separator = config.getProperty("separator");
        final String badValue = config.getProperty("badvalue");
        Integer defaultValue = null;

        if (badValue != null)
        {
            defaultValue = NumberUtils.toInt(badValue);
        }

        final TypeValue tvalue = DecoderUtil.typeValueFrom(entry, separator);
        return getIntegerValueFor(tvalue, defaultValue);
    }
}
