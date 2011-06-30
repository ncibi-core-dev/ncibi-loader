package org.ncibi.commons.smooks.tagmapper;

/**
 * Implements a TagMapper that simply passes through the given tag and performs
 * no mapping. The user can configure how to handle checking if a tag is mapped.
 * By default isMappedTag() returns false (since no actual mapping is
 * performed), however this behavior can be changed.
 * 
 * @author gtarcea
 * 
 */
public class PassthroughTagMapper implements TagMapper
{
    /**
     * What value should be returned by isMappedTag()
     */
    private boolean tagMatches;

    /**
     * Constructor. After calling this constructor isMappedTag() will return
     * false.
     */
    public PassthroughTagMapper()
    {
        tagMatches = false;
    }

    /**
     * Constructor. Allows the user to configure the return for isMappedTag().
     * 
     * @param tagMatches
     */
    public PassthroughTagMapper(final boolean tagMatches)
    {
        this.tagMatches = tagMatches;
    }

    /**
     * Setter - Sets the value that isMappedTag() will return.
     * 
     * @param tagMatches
     *            The value isMappedTag() should return.
     * @return this.
     */
    public PassthroughTagMapper setTagMatches(final boolean tagMatches)
    {
        this.tagMatches = tagMatches;
        return this;
    }

    /**
     * By default returns false. Can be configured by the user to instead return
     * True. See setTagMatches() and the list of Constructors for the class.
     */
    @Override
    public boolean isMappedTag(final String tag)
    {
        return tagMatches;
    }

    /**
     * Returns the tag passed in. Doesn't perform any mapping.
     */
    @Override
    public String mapTag(final String tag)
    {
        return tag;
    }

}
