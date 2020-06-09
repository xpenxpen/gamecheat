package org.xpen.odinsoft.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.odinsoft.fileformat.PakFile.FatEntry;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class PakFileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        fileHandlersMap.put("shp", new ShpHandler());
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
        if (bytes.length >= 4
                && bytes[0] == 'T'
                && bytes[1] == 'L'
                && bytes[2] == 'H'
                && bytes[3] == 'S'
        ) {
            return "shp";
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
