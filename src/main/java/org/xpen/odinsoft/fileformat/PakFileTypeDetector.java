package org.xpen.odinsoft.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.odinsoft.fileformat.PakFile.FatEntry;

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
