package org.xpen.hantang.fd.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.hantang.fd.fileformat.DatfFile.FatEntry;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class DatfFileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", false));
        fileHandlersMap.put("dato", new DatoHandler());
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
    	
//        if (entry.datFileName.equals("DATO")) {
//            return "dato";
//        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
