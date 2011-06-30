package org.ncibi.commons.smooks.reader.kegg;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ncibi.commons.test.utils.TestUtils

class KeggReaderTest
{

    @Test
    public void testKeggCompoundReader()
    {
        String results = TestUtils.runSmooks("smooks-kegg-test.xml", "test-data/keggcompound.txt");
        //println(results)
        assertTrue(results != null)
    }

}
