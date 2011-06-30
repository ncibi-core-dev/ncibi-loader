package org.ncibi.commons.smooks.reader.excel;

import java.io.IOException;
import java.util.Date;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.milyn.cdr.annotation.ConfigParam;
import org.milyn.delivery.StreamReader;
import org.ncibi.commons.smooks.reader.AbstractSmooksXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A Smooks reader for Excel files. This reader is configured similar to the current Smooks
 * CSVReader, except that you cannot implicitly create a bean with it. There are two xml annotations
 * that are setup:
 * <nl/>
 * fields - Gives a name to each column in the excel worksheet.
 * <nl/>
 * worksheet - Specifies the worksheet to read in the excel file.
 * <nl/>
 * The field name will be used as the SAX tag.
 * 
 * @author gtarcea
 */
public class ExcelReader extends AbstractSmooksXMLReader implements StreamReader
{
    /**
     * The record element where a record is a row in the spreadsheet.
     */
    private final static String EXCEL_RECORD_ELEMENT = "excel-record";

    /**
     * A comma separated list of field names.
     */
    @ConfigParam(name = "fields")
    private String fields;

    /**
     * The names in fields separated out.
     */
    private String[] excelFields;

    /**
     * The worksheet to open in the excel file.
     */
    @ConfigParam(name = "worksheet")
    private String worksheet;

    /**
     * Number of rows to skip before beginning to process data in the excel worksheet.
     */
    @ConfigParam(name = "skipLines")
    private int skipLines;

    /**
     * Called to process the Excel file. The Excel file has been attached to the input source.
     */
    @Override
    public void processInput(final InputSource input)
    {
        final Sheet sheet = openExcelWorksheet(input);
        readWorksheet(sheet);
    }

    /**
     * Reader specific setup.
     */
    @Override
    public void setup()
    {
        String[] tmpFields = fields.split(",");
        excelFields = new String[tmpFields.length];

        int index = 0;
        for (String field : tmpFields)
        {
            excelFields[index] = field.trim();
            index++;
        }
    }

    /**
     * Opens the excel worksheet configured in worksheet.
     * 
     * @param input
     *            The excel file.
     * @return The worksheet.
     */
    private Sheet openExcelWorksheet(final InputSource input)
    {
        Workbook workbook = null;

        try
        {
            workbook = WorkbookFactory.create(input.getByteStream());
        }
        catch (final InvalidFormatException e)
        {
            throw new IllegalArgumentException("Workbook could not be opened.", e);
        }
        catch (final IOException e)
        {
            throw new IllegalArgumentException("Workbook could not be opened.", e);
        }

        return workbook.getSheet(worksheet);
    }

    /**
     * Reads the worksheet and processes each entry.
     * 
     * @param sheet
     *            The worksheet to process.
     */
    private void readWorksheet(final Sheet sheet)
    {
        try
        {
            final int lastRowNum = sheet.getLastRowNum();
            for (int row = sheet.getFirstRowNum(); row <= lastRowNum; row++)
            {
                if (row < skipLines)
                {
                    /*
                     * If there are lines we should skip because they are column headers in the
                     * excel worksheet, then just keep iterating.
                     */
                    continue;
                }

                startTag(EXCEL_RECORD_ELEMENT);
                readRow(sheet.getRow(row));
                endTag(EXCEL_RECORD_ELEMENT);
            }
        }
        catch (final SAXException e)
        {
            throw new IllegalStateException("SAX error encountered while processing excel file.", e);
        }
    }

    /**
     * Convert a cells contents into a string.
     * 
     * @param c
     *            The cell to convert the contents for.
     * @return The cell contents as a string. If type is unsupported returns the string
     *         "UNSUPPORTED_TYPE".
     */
    private String getCellContents(Cell c)
    {
        String cellValue = "";

        switch (c.getCellType())
        {
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                Boolean b = c.getBooleanCellValue();
                cellValue = b.toString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(c))
                {
                    Date d = c.getDateCellValue();
                    cellValue = d.toString();
                }
                else
                {
                    Double d = c.getNumericCellValue();
                    Long l = d.longValue();
                    cellValue = l.toString();
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = c.getRichStringCellValue().getString();
                break;
            default:
                cellValue = "UNSUPPORTED_TYPE";
                break;
        }

        return cellValue;
    }

    /**
     * Processes a row.
     * 
     * @param r
     *            The row to process.
     * @throws SAXException
     *             When SAX cannot process a tag.
     */
    private void readRow(final Row r) throws SAXException
    {
        for (final Cell c : r)
        {
            final String contents = getCellContents(c);
            sendCellContents(c.getColumnIndex(), contents);
        }
    }

    /**
     * Calls fireTag on a particular cell entry. fireTag is only called when the index given is a
     * know field, otherwise it is ignored.
     * 
     * @param index
     *            The index into excelFields to get the column tag name.
     * @param contents
     *            The contents for the tag.
     * @throws SAXException
     *             When SAX cannot process a tag.
     */
    private void sendCellContents(final int index, final String contents) throws SAXException
    {
        if (index >= 0 && index < excelFields.length)
        {
            final String tag = excelFields[index];
            fireTag(tag, contents);
        }
    }
}
