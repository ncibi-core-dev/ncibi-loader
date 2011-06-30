package org.ncibi.commons.smooks.tagmapper;

import org.ncibi.commons.io.DelimitedLineDataLoader;
import org.ncibi.commons.lang.StrUtils;

/**
 * Maps tags to regular expression matches. The tag mapping is configured in a
 * file with the following format:
 * <nl>
 * regular-expression:::tag
 * 
 * @author gtarcea
 * 
 */
public class WildcardMatchingTagMapper implements TagMapper
{
    /**
     * Internal class used to hold an tag map entry from the tag map
     * configuration file.
     * 
     * @author gtarcea
     * 
     */
    private static class TagMapEntry
    {
        /**
         * The regular expression tag to match against.
         */
        public String expression;

        /**
         * The mapping for the tag.
         */
        public String mappedToTag;

        /**
         * Constructor. Creates a tag map entry with tag and mappedToTag set to
         * the passed in settings.
         * 
         * @param expressio
         *            Sets this.tag to tag
         * @param mappedToTag
         *            Sets this.mappedToTag to mappedToTag.
         */
        public TagMapEntry(final String expression, final String mappedToTag)
        {
            this.expression = expression;
            this.mappedToTag = mappedToTag;
        }
    }

    /**
     * The loader used to read in the tag map configuration file.
     */
    private final DelimitedLineDataLoader<TagMapEntry> tagmapLoader;

    /**
     * Constructor. Loads the tagmap file passed in.
     * 
     * @param tagmapFile
     *            The tagmap file to load and parse.
     */
    public WildcardMatchingTagMapper(final String tagmapFile)
    {
        tagmapLoader = new DelimitedLineDataLoader<TagMapEntry>(tagmapFile)
        {
            @Override
            protected TagMapEntry parseLine(final String line)
            {
                if (line != null && line.length() > 0)
                {
                    final String[] linePieces = StrUtils.splitAsArray(line, ":::");
                    if (linePieces != null && linePieces.length == 2)
                    {
                        return new TagMapEntry(linePieces[0], linePieces[1]);
                    }
                }
                return null;
            }

        };

        tagmapLoader.setHeaderLineCount(0);
        tagmapLoader.loadData();
    }

    /**
     * Searches through the list of tags for a match.
     * 
     * @param tag
     *            The tag to match on.
     * @return The TagMapEntry matching the tag or null if no match found.
     */
    private TagMapEntry findMatchingEntry(final String tag)
    {
        for (final TagMapEntry mapEntry : tagmapLoader.getDataItems())
        {
            if (tag.matches(mapEntry.expression))
            {
                return mapEntry;
            }
        }
        return null;
    }

    /**
     * Checks for a match against the tag.
     */
    @Override
    public boolean isMappedTag(final String tag)
    {
        if (findMatchingEntry(tag) == null)
        {
            return false;
        }

        return true;
    }

    /**
     * Maps the specified tag to its mappedToTag entry. If not match is found it
     * just returns the tag string passed in.
     */
    @Override
    public String mapTag(final String tag)
    {
        final TagMapEntry matchingEntry = findMatchingEntry(tag);
        if (matchingEntry == null)
        {
            return tag;
        }
        return matchingEntry.mappedToTag;
    }

}
