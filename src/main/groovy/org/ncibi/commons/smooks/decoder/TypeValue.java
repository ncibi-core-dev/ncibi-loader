package org.ncibi.commons.smooks.decoder;


/**
 * A class for representing type values. For example an entry in a file might have
 * <nl>
 * pmid:1234
 * </nl>
 * This class allows this entry to be split apart and represented as two separate values.
 * 
 * @author gtarcea
 *
 */
public final class TypeValue
{   
    /**
     * The type of the value.
     */
    private final String type;
    
    /**
     * The value.
     */
    private final String value;
    
    /**
     * Constructor.
     * @param type The type.
     * @param value The value.
     */
    public TypeValue(final String type, final String value)
    {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Getter.
     * @return The type.
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Getter.
     * @return The value.
     */
    public String getValue()
    {
        return this.value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof TypeValue))
        {
            return false;
        }
        TypeValue other = (TypeValue) obj;
        if (type == null)
        {
            if (other.type != null)
            {
                return false;
            }
        }
        else if (!type.equals(other.type))
        {
            return false;
        }
        if (value == null)
        {
            if (other.value != null)
            {
                return false;
            }
        }
        else if (!value.equals(other.value))
        {
            return false;
        }
        return true;
    }
}
