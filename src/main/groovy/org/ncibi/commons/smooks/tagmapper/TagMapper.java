package org.ncibi.commons.smooks.tagmapper;

/**
 * An interface that defines a mapper for tags. If there are a set of tags from
 * the incoming source that need to be mapped to a different name then an
 * instance of this interface should be defined.
 * 
 * @author gtarcea
 * 
 */
public interface TagMapper
{
    /**
     * Given a tag maps that tag to a new name.
     * 
     * @param tag
     *            The tag to map.
     * @return The mapped tag, or the original tag if there is no mapping.
     */
    public abstract String mapTag(final String tag);

    /**
     * Given a tag checks to see if this is a mapped tag.
     * 
     * @param tag
     *            The tag to check.
     * @return True if the tag is mapped, false otherwise.
     */
    public abstract boolean isMappedTag(final String tag);
}
