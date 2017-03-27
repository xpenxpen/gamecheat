package org.xpen.farcry3;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.Fat2File;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.dunia2.fileformat.fat2.FileListManager;

public class FatExtracter {
    
    private static final Logger LOG = LoggerFactory.getLogger(FatExtracter.class);

    public static void main(String[] args) throws Exception {
        Fat2File fat2File = new Fat2File(new File("igepatch.fat"));
        fat2File.decode();
        fat2File.close();
        
        List<Entry> entries = fat2File.getEntries();
        for (Entry entry : entries) {
            LOG.debug(entry.toString());
        }
        
        FileListManager flm = new FileListManager();
        flm.load(FatExtracter.class.getClassLoader().getResourceAsStream("farcry3/files/igepatch.filelist"));
        Map<Long, String> crcMap = flm.getCrcMap();

    }

}
