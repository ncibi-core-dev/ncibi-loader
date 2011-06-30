package org.ncibi.commons.smooks.reader.fasta

import org.ncibi.commons.smooks.reader.AbstractSmooksXMLReader
import org.xml.sax.InputSource;

/**
 * An implementation of the SmooksXMLReader for reading FASTA format files.
 */
public class FastaReader extends AbstractSmooksXMLReader 
{   
    /**
     * The record tag.
     */
    private static def FASTA_RECORD_ELEMENT = "fasta-record"
    
    /**
     * The idtype tag.
     */
    private static def FASTA_IDTYPE_ELEMENT = "idtype"
    
    /**
     * The idvalue tag.
     */
    private static def FASTA_IDVALUE_ELEMENT = "idvalue"
    
    /**
     * The description tag.
     */
    private static def FASTA_DESCRIPTION_ELEMENT = "description"
    
    /**
     * The sequence tag.
     */
    private static def FASTA_SEQUENCE_ELEMENT = "sequence"
    
    
    /**
     * Constructor.
     */
    public FastaReader()
    {
        super();
    }
    
    /**
     * Processes the fasta input file, firing off events on simulated tags. The fasta
     * file is treated as an XML file, with tags for ids, descriptions, and the sequence.
     */
    public void processInput(final InputSource input) 
    {
        final BufferedReader bufferedReader = new BufferedReader(input.getCharacterStream())  
        def last
        
        bufferedReader.eachLine { line ->
            if (line ==~ /^>.*/) 
            {
                startTag(FastaReader.FASTA_RECORD_ELEMENT)
                /*
                 * Description/id's line 
                 */
                def items = (line - ">").tokenize("|")
                last = items.size()
                items.eachWithIndex { str, index ->
                    if (index == last - 1 && last % 2 == 1) 
                    {
                        // Last entry is the description
                        fireTag(FastaReader.FASTA_DESCRIPTION_ELEMENT, str)
                    }
                    else if (index % 2 == 0) 
                    {
                        // Even numbers are the id type
                        fireTag(FastaReader.FASTA_IDTYPE_ELEMENT, str)
                    }
                    else 
                    {
                        // Odd index numbers are the id value 
                        fireTag(FastaReader.FASTA_IDVALUE_ELEMENT, str)
                    }
                }
            }
            else
            {
                /*
                 * Sequence line, ignore blank lines.
                 */
                if (line)
                {
                    fireTag(FastaReader.FASTA_SEQUENCE_ELEMENT, line)
                    endTag(FastaReader.FASTA_RECORD_ELEMENT)
                }		
            }
        }
    }
}
