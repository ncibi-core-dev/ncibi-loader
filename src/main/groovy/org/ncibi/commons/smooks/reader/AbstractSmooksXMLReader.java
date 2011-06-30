package org.ncibi.commons.smooks.reader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.milyn.container.ExecutionContext;
import org.milyn.xml.SmooksXMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Base class for implementing a SmooksXMLReader. This class provides an overall framework for
 * implementing the reader, including supplying default logic and utility routines that can be used
 * by reader implementers.
 * 
 * @author gtarcea
 */
public abstract class AbstractSmooksXMLReader implements SmooksXMLReader
{

    /**
     * Processes each line of the input source.
     * 
     * @param input
     *            The input source to process.
     */
    public abstract void processInput(final InputSource input) throws IOException, SAXException;

    /**
     * The document element tag to use at the start of processing.
     */
    private final static String DOCUMENT_ELEMENT = "document";
    
    /**
     * The top level element to use for a list of items.
     */
    private final static String RECORD_LIST_ELEMENT = "record-list";

    /**
     * SAX contentHandler to fire events on.
     */
    private ContentHandler contentHandler = null;

    /**
     * The Smooks execution context.
     */
    private ExecutionContext executionContext = null;

    /**
     * The Smooks error handler.
     */
    private ErrorHandler errorHandler = null;

    /**
     * The Smooks entity resolver.
     */
    private EntityResolver entityResolver = null;

    /**
     * The Smooks dtd handler.
     */
    private DTDHandler dTDHandler = null;

    /**
     * Properties used by Smooks/SAX
     */
    private final Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Features used by Smooks/SAX
     */
    private final Map<String, Boolean> features = new HashMap<String, Boolean>();

    /**
     * Constant for passing in the attributes for XML setup.
     */
    protected static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

    /**
     * Allows for reader specific setup.
     */
    public void setup()
    {
        // Can be overridden.
    }

    /**
     * A convenience routine for firing off events on tags.
     * 
     * @param tag
     *            The tag to fire off.
     * @param tagValue
     *            The value of the tag element.
     * @throws SAXException
     */
    protected final void fireTag(final String tag, final String tagValue) throws SAXException
    {
        startTag(tag);
        contentHandler.characters(tagValue.toCharArray(), 0, tagValue.length());
        endTag(tag);
    }

    /**
     * Starts a tag.
     * 
     * @param tag
     *            The tag to start.
     * @throws SAXException
     */
    protected final void startTag(final String tag) throws SAXException
    {
        contentHandler.startElement(XMLConstants.NULL_NS_URI, tag, "", EMPTY_ATTRIBUTES);
    }

    /**
     * Ends a tag.
     * 
     * @param tag
     *            The tag to end.
     * @throws SAXException
     */
    protected final void endTag(final String tag) throws SAXException
    {
        contentHandler.endElement(XMLConstants.NULL_NS_URI, tag, "");
    }

    /**
     * Processes a document by firing off the initial SAX events to start a document, and to start a
     * record list.
     * 
     * @param input
     *            The input source
     * @throws IOException
     * @throws SAXException
     */
    private void processDocument(final InputSource input) throws IOException, SAXException
    {
        /*
         * Fire off the start event.
         */
        contentHandler.startDocument();
        startTag(DOCUMENT_ELEMENT);

        /*
         * Add an extra element not in the file to make it easier to use this reader to configure a
         * java bean with a list of separate record entries.
         */
        startTag(RECORD_LIST_ELEMENT);

        /*
         * Process the input source.
         */
        processInput(input);

        /*
         * Fire ending events.
         */
        endTag(RECORD_LIST_ELEMENT);
        endTag(DOCUMENT_ELEMENT);
        contentHandler.endDocument();
    }

    /**
     * The SmooksXMLReader implementation that parses the input source. This has been expanded to do
     * some sanity checking on the contentHandler and executionContent as well as adding in calls
     * to setup() for reader specific setup.
     */
    public final void parse(final InputSource input) throws IOException, SAXException
    {
        /*
         * Perform some sanity checks
         */
        if (contentHandler == null)
        {
            throw new IllegalStateException("'contentHandler' not set. Cannot parse stream.");
        }

        if (executionContext == null)
        {
            throw new IllegalStateException(
                    "Smooks container 'executionContext' not set. Cannot parse stream.");
        }

        setup();
        processDocument(input);
    }

    /****************************************************
     * Commonly unimplemented/unsupported routines. All
     * these routines can be overridden.
     */

    /**
     * Another parse method - Not supported.
     */
    public void parse(final String systemId) throws IOException, SAXException
    {
        throw new UnsupportedOperationException("Operation not supports by this reader.");
    }

    /**
     * Setter
     */
    public void setFeature(final String name, final boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException
    {
        features.put(name, value);
    }

    /**
     * Getter
     */
    public boolean getFeature(final String name) throws SAXNotRecognizedException,
            SAXNotSupportedException
    {
        final Boolean feature = features.get(name);
        return feature != null && feature;
    }

    /**
     * Setter
     */
    public void setProperty(final String name, final Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException
    {
        properties.put(name, value);
    }

    /**
     * Getter
     */
    public Object getProperty(final String name) throws SAXNotRecognizedException,
            SAXNotSupportedException
    {
        final Object property = properties.get(name);
        if (property == null)
        {
            throw new SAXNotRecognizedException("Unknown feature: " + name);
        }

        return property;
    }

    public void setEntityResolver(final EntityResolver resolver)
    {
        entityResolver = resolver;
    }

    public EntityResolver getEntityResolver()
    {
        return entityResolver;
    }

    public void setDTDHandler(final DTDHandler handler)
    {
        dTDHandler = handler;
    }

    public DTDHandler getDTDHandler()
    {
        return dTDHandler;
    }

    public void setContentHandler(final ContentHandler handler)
    {
        contentHandler = handler;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public void setErrorHandler(final ErrorHandler handler)
    {
        errorHandler = handler;
    }

    public ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    public void setExecutionContext(final ExecutionContext executionContext)
    {
        this.executionContext = executionContext;
    }
}
