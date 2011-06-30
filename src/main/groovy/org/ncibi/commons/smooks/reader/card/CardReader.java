package org.ncibi.commons.smooks.reader.card;

import java.io.BufferedReader;
import java.io.IOException;

import org.milyn.cdr.annotation.ConfigParam;
import org.ncibi.commons.io.FileUtilities;
import org.ncibi.commons.smooks.reader.AbstractSmooksXMLReader;
import org.ncibi.commons.smooks.tagmapper.PassthroughTagMapper;
import org.ncibi.commons.smooks.tagmapper.TagMapper;
import org.ncibi.commons.smooks.tagmapper.WildcardMatchingTagMapper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements a Smooks cartridge for reading Card style files. Card
 * files are one of the download formats for the metabolomics data from
 * www.hmdb.ca. For example the format of a MetaboCard file is as follows:
 * <nl>
 * #BEGIN_METABOCARD <hmdb id>
 * <nl>
 * # fieldName:
 * <nl>
 * fieldValue (1 or more lines)
 * <nl>
 * ...
 * <nl>
 * #END_METABOCARD <hmdb id>
 * <nl>
 * Some values for fields are multiple lines, for example sequences. In this
 * case all the lines are returned (with a newline separating each line) as one
 * element.
 * 
 * This reader creates an output event stream that looks like:
 * <nl>
 * <metabocard-doc>
 * <nl>
 * <metabocard-list>
 * <nl>
 * 0 or more: <metabocard-rec>...</metabocard-rec>
 * <nl>
 * </metabocard-list>
 * <nl>
 * </metabocard-doc>
 * <nl>
 * There are special tags emitted that do not directly appear in the metabocard
 * file. The first tag is metabocard-list, this denotes the beginning of a list
 * of metabocard-doc entries. The metabocard file does not break these entries
 * into a separate this. This entry exists to make it easy to create a javabean
 * with an array of card entries. The second tag is hmdb_id. The hmdb id is
 * actually contained on a #BEGIN_METABOCARD line in the metabocard file. This
 * line is used to denote the beginning of a record. This class extracts the id
 * from this line and after firing off a metabocard-rec event, it immediately
 * fires off a hmdb_id event containing the HMDB id.
 * 
 * @author gtarcea
 * 
 */
public class CardReader extends AbstractSmooksXMLReader
{
    /**
     * The opening tag for a metabocard entry.
     */
    private static final String CARD_RECORD_EL = "card-rec";

    /**
     * For multi-line value string separate each line using the following
     * character (defaults to new-line)
     */
    @ConfigParam(defaultVal = "\n")
    private char separator;

    /**
     * The file name or fullpath to the tagmap file. If a full path isn't
     * specified then we check for the file in the classes System Resource path.
     */
    @ConfigParam(defaultVal = "")
    private String tagmap;

    /**
     * The mapper to use to map tags. If no mapping file is specified then this
     * will use the PassthroughTagMapper() which does no mapping.
     */
    private TagMapper tagMapper;

    /**
     * Sets up the tagMapper to perform tagmapping of tags read in from the
     * input file.
     * 
     * @throws IllegalArgumentException
     *             If a tagmap file is specified but does not exist.
     */
    @Override
    public void setup()
    {
        if ("".equals(tagmap))
        {
            tagMapper = new PassthroughTagMapper();
        }
        else
        {
            /*
             * If the tagmap file given doesn't exist then look for it in the
             * class path.
             */
            if (!FileUtilities.fileExists(tagmap))
            {
                /*
                 * If tagmap can't be found by getSystempath(), then the
                 * getSystemPath() utility will throw an
                 * IllegalArgumentException.
                 */
                tagmap = FileUtilities.getSystemPath(tagmap);
            }

            tagMapper = new WildcardMatchingTagMapper(tagmap);
        }
    }

    /**
     * Takes a line and tests if it is a name field. If it is a name field then
     * it cleans up the name and returns it. Otherwise it returns NULL. For
     * record fields it fire off the record start/end events.
     * 
     * @param line
     *            The line to
     * @return Cleaned up field name or null if line is not a field name or is a
     *         begin/end record line.
     * @throws SAXException
     *             Doesn't actually get thrown.
     */
    private String processFieldName(final String line) throws SAXException
    {
        String fieldName = null;

        /*
         * We are either on a named field or at the beginning/end of a card
         * entry.
         */
        if (line.startsWith("#BEGIN_"))
        {
            startTag(CARD_RECORD_EL);

            /*
             * Also fire off an event with the metabocard hmdb (contained on the
             * #BEGIN line)
             */
            final int onePastSpace = line.indexOf(' ') + 1;
            final String id = line.substring(onePastSpace);
            final String tag = tagMapper.mapTag("hmdb_id");
            fireTag(tag, id);
        }
        else if (line.startsWith("#END_"))
        {
            endTag(CARD_RECORD_EL);
        }
        else
        {
            /*
             * Remove '# ' (notice space) at beginning of line and ':' at end
             */
            final int colon = line.indexOf(':');
            fieldName = line.substring(2, colon);
        }

        return fieldName;
    }

    /**
     * Performs processing of the individual items in the document.
     * 
     * @param input
     *            The document input source stream.
     * @throws IOException
     *             When their is a problem reading the input source.
     * @throws SAXException
     *             When their is a problem writing to SAX.
     */
    @Override
    public void processInput(final InputSource input) throws IOException, SAXException
    {
        final BufferedReader bufferedReader = new BufferedReader(input.getCharacterStream());
        final StringBuffer fieldValue = new StringBuffer();
        String fieldName = null;

        String line = bufferedReader.readLine();

        while (line != null)
        {
            /*
             * If line length greater than 0 then we are either reading a tag
             * (fieldName) or a value. Tags start with a #.
             */
            if (line.length() != 0)
            {
                if (line.charAt(0) == '#')
                {
                    fieldName = processFieldName(line);
                }
                else if (fieldName != null)
                {
                    if (fieldValue.length() != 0)
                    {
                        fieldValue.append(separator);
                    }

                    fieldValue.append(line);
                }
            }

            /*
             * If fielName isn't null, and fieldValue has data then we might be
             * able to output the field data. One more check to make is whether
             * or not the line we just read is length 0. If it is length zero
             * then we know we are past reading all field values. However, if it
             * is greater than zero and the other two checks are true, then we
             * are still reading a multi-line value and aren't yet ready to
             * output the field value.
             */
            if (fieldName != null && fieldValue.length() != 0 && line.length() == 0)
            {
                /*
                 * At this point we have seen both a field (a line starting with
                 * a '#') and read one or more lines containing the value for
                 * the field, and have now run into a line that only contains a
                 * newline. Since we have now reached the end of this field name
                 * and value pair we can fire off the events associated with
                 * this field.
                 */
                final String tag = tagMapper.mapTag(fieldName);
                fireTag(tag, fieldValue.toString());

                /*
                 * Reset state since we have already fired off the events for
                 * this field/value pair.
                 */
                fieldName = null;
                fieldValue.setLength(0);
            }

            line = bufferedReader.readLine();
        }
    }
}