package org.ncibi.commons.smooks.reader.kegg;

import org.ncibi.commons.smooks.reader.AbstractSmooksXMLReader
import org.xml.sax.InputSource

/**
 * Read a KEGG file and generate a series of events.
 * 
 * @author gtarcea
 *
 */
class KeggReader extends AbstractSmooksXMLReader
{ 
    /**
     * The Start record tag.
     */
    private final static def KEGG_RECORD_ELEMENT = "kegg-record"
    
    /**
     * Constructor.
     */
    public KeggReader()
    {
        super()
    }
    
    /**
     * Processes the input source.
     * 
     * @param input The input source to process.
     */
    public void processInput(final InputSource input)
    {
        final BufferedReader bufferedReader = new BufferedReader(input.getCharacterStream())
        String currentTag = null
        boolean fireRecordEvent = true
        
        bufferedReader.eachLine { line ->
            if (fireRecordEvent)
            {
                startTag(KeggReader.KEGG_RECORD_ELEMENT)
                /*
                 * First line contains a tag - parse it and the line out
                 * and fire that event.
                 */
                def pieces = line.replaceAll(/\s+/, '|').tokenize('|')
                currentTag = pieces[0]
                pieces[1..<pieces.size()].each { entry ->
                    fireTag(currentTag, entry)
                }
                fireRecordEvent = false
            }
            else if (line == '///')
            {
                /*
                 * End of record.
                 */
                endTag(KeggReader.KEGG_RECORD_ELEMENT)
                fireRecordEvent = true
            }
            else
            {
                if (line.size() > 0 && line[0] != ' ')
                {
                    /*
                     * We are at a new tag.
                     */
                    def space = line.indexOf(' ')
                    currentTag = line[0..<space]
                    def restOfLine = line[space..<line.size()]
                    fireTag(currentTag, restOfLine.trim())
                }
                else if (line.size() > 0 && currentTag)
                {
                    fireTag(currentTag, line.trim())
                }
            }
        }
    }
}
