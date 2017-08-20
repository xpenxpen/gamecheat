package org.xpen.softstar.pal.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.softstar.pal.fileformat.TswFile.FatEntry;

public class TswFileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        fileHandlersMap.put("wav", new WavHandler("snd", "wav", false));
        fileHandlersMap.put("tsw", new TswRleHandler());
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
        if (bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'A'
                && bytes[10] == 'V'
                && bytes[11] == 'E'
        ) {
            return "wav";
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
