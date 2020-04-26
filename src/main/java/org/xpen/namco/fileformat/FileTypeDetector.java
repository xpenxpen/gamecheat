package org.xpen.namco.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        fileHandlersMap.put("img64", new ImgHandler(32, 64));
        fileHandlersMap.put("img256", new ImgHandler(256, 256));
    }
    
    public static String detect(String fileName) {
        if (fileName.equals("all_info_main")) {
        	return "img64";
        }
        if (fileName.equals("talk_char")) {
            return "img64";
        }
        if (fileName.equals("background")) {
            return "img256";
        }
        
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
