package org.ncibi.commons.smooks.reader.sdf;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ncibi.commons.test.utils.TestUtils

class SDFReaderTest
{
    @Test
    public void testSDFReader()
    {
        String results = TestUtils.runSmooks("smooks-sdf-test.xml", "test-data/sdftest.sdf");
        String expectedResults = "<sdf-document><sdf-record-list><sdf-record><record-id>LMSL01010001</record-id><PUBCHEM_SUBSTANCE_URL>http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=4266334</PUBCHEM_SUBSTANCE_URL><LIPID_MAPS_CMPD_URL>http://www.lipidmaps.org/data/get_lm_lipids_dbgif.php?LM_ID=LMSL01010001</LIPID_MAPS_CMPD_URL><LM_ID>LMSL01010001</LM_ID><SYSTEMATIC_NAME>UDP-3-(3R-hydroxy-tetradecanoyl)-alphaD-glucosamine</SYSTEMATIC_NAME><CATEGORY>Saccharolipids [SL]</CATEGORY><MAIN_CLASS>Acylaminosugars [SL01]</MAIN_CLASS><SUB_CLASS>Monoacylaminosugars [SL0101]</SUB_CLASS><EXACT_MASS>791.26419</EXACT_MASS><FORMULA>C29H51N3O18P2</FORMULA><PUBCHEM_SID>4266334</PUBCHEM_SID><KEGG_ID>C11521</KEGG_ID></sdf-record></sdf-record-list></sdf-document>"
        assertTrue(expectedResults.equals(results));
    }
    
}
