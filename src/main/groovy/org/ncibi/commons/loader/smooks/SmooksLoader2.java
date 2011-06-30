package org.ncibi.commons.loader.smooks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.milyn.scribe.register.DaoRegister;
import org.milyn.scribe.register.MapDaoRegister;
import org.ncibi.commons.io.FileInputLineProcessor;
import org.ncibi.commons.smooks.loader.DaoDataSourceLoader;
import org.ncibi.commons.smooks.loader.DataSourceDao;
import org.ncibi.commons.smooks.loader.DataSourceLoader;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class SmooksLoader2
{
    private final DaoRegister<DataSourceDao> daoRegister;
    private final DataSourceDao dao;
    private final String loader;

    public SmooksLoader2(String loader, String smooksDaoName, DataSourceDao dao)
    {
        this.loader = loader;
        this.dao = dao;
        MapDaoRegister.Builder<DataSourceDao> builder = MapDaoRegister.builder();
        daoRegister = builder.put(smooksDaoName, dao).build();
    }

    public void runInitAndLoad()
    {

    }

    public void load()
    {
        FileInputLineProcessor inputProcessor = new FileInputLineProcessor()
        {
            @Override
            public void processLine(String line) throws IOException
            {
                loadDataSource(line);
            }
        };
        
        inputProcessor.process("");
    }

    private void loadDataSource(String line) throws IOException
    {
        if (commentLine(line)) { return; }

        DataSourceConfig dsConfig = createDataSourceConfigFromLine(line);
        DataSourceLoader loader = new DaoDataSourceLoader(null, this.daoRegister, "path");
        for (File f : getAllFilesMatching(dsConfig.datasourceDir.toString(), dsConfig.fileMatcher))
        {
            InputStream inputStream = openAsInputStream(f);
            loader.load(inputStream, null);
        }
    }

    public final InputStream openAsInputStream(File f) throws IOException
    {

        InputStream inputStream = FileUtils.openInputStream(f);
        String fileName = f.toString();
        if (fileName.endsWith(".gz"))
        {
            return new GZIPInputStream(inputStream);
        }
        else
        {
            return inputStream;
        }

    }

    Collection<File> getAllFilesMatching(String directoryName, String extension)
    {
        File directory = new File(directoryName);

        @SuppressWarnings("unchecked")
        Collection<File> files = (Collection<File>) FileUtils.listFiles(directory,
                    new WildcardFileFilter(extension), null);
        return files;
    }

    private static class DataSourceConfig
    {
        public String datasourceName;
        public String smooksFileName;
        public File datasourceDir;
        public String fileMatcher;
    }

    private DataSourceConfig createDataSourceConfigFromLine(String line)
    {
        DataSourceConfig dsConfig = new DataSourceConfig();

        Iterable<String> linePieces = Splitter.on('=').omitEmptyStrings().trimResults().split(line);
        dsConfig.datasourceName = Iterables.get(linePieces, 0);
        dsConfig.smooksFileName = Iterables.get(linePieces, 0);
        dsConfig.datasourceDir = new File(Iterables.get(linePieces, 0));
        dsConfig.fileMatcher = Iterables.get(linePieces, 0);
        if (dsConfig.fileMatcher.startsWith("*"))
        {
            dsConfig.fileMatcher = "." + dsConfig.fileMatcher;
        }

        return dsConfig;
    }

    private boolean commentLine(String line)
    {
        return line.startsWith("#");
    }
}
