package org.ncibi.commons.smooks.reader.sdf

import org.ncibi.commons.smooks.reader.AbstractSmooksXMLReader
import org.xml.sax.InputSource

/**
 *
 * @author gtarcea
 */
class SDFReader extends AbstractSmooksXMLReader
{
    private final static def SDF_RECORD_ELEMENT = "sdf-record"

    /**
     * Constructor.
     */
    public SDFReader()
    {
        super();
    }

    /**
     * Processes the SDF input file, firing off events on simulated tags.
     */
    public void processInput(final InputSource input)
    {
        final BufferedReader bufferedReader = new BufferedReader(input.getCharacterStream())
        def fireRecordEvent = true
        def tagToFire = null
        bufferedReader.eachLine { line ->
            if (fireRecordEvent)
            {
                startTag(SDFReader.SDF_RECORD_ELEMENT)
                fireTag("record-id", line)
                fireRecordEvent = false
            }
            else
            {
                if (line == '$$$$')
                {
                    endTag(SDFReader.SDF_RECORD_ELEMENT)
                    fireRecordEvent = true
                }
                else
                {
                    if (tagToFire)
                    {
                        fireTag(tagToFire, line)
                        tagToFire = null
                    }
                    else if (line.size() > 0 && line[0] == '>')
                    {
                        def left = line.indexOf("<")
                        if (left == -1) { return }
                        def right = line.indexOf(">", left)
                        if (right == -1) { return }
                        tagToFire = line[left+1..<right]
                    }
                    /* else:
                     * We ignore other lines
                     */
                }
            }
        }
    }
}

