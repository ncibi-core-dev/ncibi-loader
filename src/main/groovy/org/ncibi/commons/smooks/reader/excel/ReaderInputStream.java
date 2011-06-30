package org.ncibi.commons.smooks.reader.excel;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Writer;

public class ReaderInputStream extends InputStream
{
    private final Reader reader;
    private final Writer writer;
    private final PipedInputStream inPipe;

    public ReaderInputStream(final Reader reader) throws IOException
    {
        this(reader, null);
    }

    public ReaderInputStream(final Reader reader, final String encoding) throws IOException
    {
        synchronized (this)
        {
            this.reader = reader;
            inPipe = new PipedInputStream();
            final OutputStream outPipe = new PipedOutputStream(inPipe);
            writer = encoding == null ? new OutputStreamWriter(outPipe) : new OutputStreamWriter(
                    outPipe, encoding);
        }
        new Thread(new Copier()).start();
    }

    @Override
    public int read() throws IOException
    {
        return inPipe.read();
    }

    @Override
    public int read(final byte b[]) throws IOException
    {
        return inPipe.read(b);
    }

    @Override
    public int read(final byte b[], final int off, final int len) throws IOException
    {
        return inPipe.read(b, off, len);
    }

    @Override
    public long skip(final long n) throws IOException
    {
        return inPipe.skip(n);
    }

    @Override
    public int available() throws IOException
    {
        return inPipe.available();
    }

    @Override
    public synchronized void close() throws IOException
    {
        close(reader);
        close(writer);
        close(inPipe);
    }

    private static void close(final Closeable cl)
    {
        try
        {
            cl.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    private class Copier implements Runnable
    {
        public void run()
        {
            final char[] buffer = new char[8192];
            try
            {
                while (true)
                {
                    int n;
                    synchronized (ReaderInputStream.this)
                    {
                        n = reader.read(buffer);
                    }
                    if (n == -1)
                    {
                        break;
                    }
                    synchronized (ReaderInputStream.this)
                    {
                        writer.write(buffer, 0, n);
                        writer.flush();
                    }
                }
            }
            catch (final IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                close(reader);
                close(writer);
            }
        }
    }
}