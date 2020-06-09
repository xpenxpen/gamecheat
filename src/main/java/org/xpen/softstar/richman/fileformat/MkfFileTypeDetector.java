package org.xpen.softstar.richman.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.softstar.richman.fileformat.MkfFile.FatEntry;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class MkfFileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", false));
        fileHandlersMap.put("wav", new SimpleCopyHandler("wav", false));
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
