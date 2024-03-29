package org.xpen.softstar.pal.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.softstar.pal.fileformat.MkfFile.FatEntry;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class MkfFileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", false));
        fileHandlersMap.put("yj1", new Yj1Handler());
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
    	
        if (bytes.length >= 4
	        && bytes[0] == 'Y'
	        && bytes[1] == 'J'
	        && bytes[2] == '_'
	        && bytes[3] == '1') {
            return "yj1";
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
