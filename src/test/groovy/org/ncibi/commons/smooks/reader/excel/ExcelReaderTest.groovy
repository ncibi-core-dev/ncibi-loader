package org.ncibi.commons.smooks.reader.excel;

import org.junit.Test;

import static org.junit.Assert.*;
import org.ncibi.commons.test.utils.TestUtils;

class ExcelReaderTest
{
    @Test
    public void testExcelReader()
    {
        String results = TestUtils.runSmooks("smooks-excel-test.xml", "test-data/exceltest.xls");
        //println("results = ${results}")
        String expectedResults = "<document><record-list><excel-record><a>a1</a><b>b1</b></excel-record><excel-record><b>b2</b></excel-record></record-list></document>"
        assertTrue(expectedResults.equals(results));
    }
}
