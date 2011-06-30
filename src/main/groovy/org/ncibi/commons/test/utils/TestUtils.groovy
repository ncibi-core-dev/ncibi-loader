package org.ncibi.commons.test.utils;

import java.io.InputStream;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.SmooksUtil;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

class TestUtils
{
    public static InputStream getAsStream(String filename)
    {
        return ClassLoader.getSystemResourceAsStream(filename);
    }
    
    public static String getAsResourceString(String filename)
    {
        return ClassLoader.getSystemResource(filename).getFile();
    }
    
    public static String runSmooks(String configFile, String dataFile) throws SmooksException, IOException, SAXException
    {
        Smooks smooks = new Smooks(getAsStream(configFile));
        ExecutionContext context = smooks.createExecutionContext();
        String results = SmooksUtil.filterAndSerialize(context, getAsStream(dataFile), smooks);
        
        return results;
    }
}
