package org.xpen.ubisoft.dunia2.fileformat.fat2;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileListManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileListManager.class);
    
    private Map<Long, String> crcMap = new HashMap<Long, String>();
    public List<String> matchList = new ArrayList<String>();
    public boolean hasSubFat;
    public List<String> subFatList = new ArrayList<String>();
    
    public void load(InputStream is) throws Exception {
        List<String> readLines = IOUtils.readLines(is, Charset.forName("UTF-8"));
        for (String line : readLines) {
            if (line.startsWith(";")) {
                continue;
            }
            String replaceStr = line.replace('/', '\\').toLowerCase(Locale.ENGLISH);
            long crc = new CRC64().update(replaceStr);
            
            if (crcMap.containsKey(crc)) {
                throw new RuntimeException("dulplicate CRC:"+crc);
            }
            crcMap.put(crc, replaceStr);
            if (replaceStr.startsWith("subfat")) {
                hasSubFat = true;
                subFatList.add(replaceStr);
            }
            LOG.debug("crc={},({}) replaceStr={}", crc, Long.toHexString(crc), replaceStr);
        }
    }

    public Map<Long, String> getCrcMap() {
        return crcMap;
    }

    public void addMatch(String str) {
        matchList.add(str);
    }
    
    
}
