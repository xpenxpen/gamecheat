package org.xpen.kingsoft.fileformat;

import java.util.HashMap;
import java.util.Map;

import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.kingsoft.fileformat.PakFile.FatEntry;

public class PakFileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", false));
        //fileHandlersMap.put("tpl", new TplHandler("tpl", false));
        fileHandlersMap.put("bmp", new SimpleCopyHandler("bmp", false));
        fileHandlersMap.put("jfif", new SimpleCopyHandler("jfif", false));
        fileHandlersMap.put("txt", new SimpleCopyHandler("txt", false));
        fileHandlersMap.put("wav", new SimpleCopyHandler("wav", false));
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
        if (entry.datFileName.equals("ini")) {
        	return "txt";
        }
        if (entry.datFileName.equals("Patch")) {
        	return "txt";
        }
        if (entry.datFileName.equals("script")) {
        	return "txt";
        }
    	
        if (bytes.length >= 4
	        && bytes[0] == 'B'
	        && bytes[1] == 'M'
	        && bytes[2] == '8'
	        && bytes[3] == '0') {
            return "bmp";
        }
        if (bytes.length >= 10
	        && bytes[0] == (byte)0xFF
	        && bytes[1] == (byte)0xD8
	        && bytes[2] == (byte)0xFF
	        && bytes[3] == (byte)0xE0
	        && bytes[6] == 'J'
	        && bytes[7] == 'F'
	        && bytes[8] == 'I'
	        && bytes[9] == 'F') {
            return "jfif";
        }
        if (bytes.length >= 12
            && bytes[0] == 'R'
            && bytes[1] == 'I'
            && bytes[2] == 'F'
            && bytes[3] == 'F'
            && bytes[8] == 'W'
            && bytes[9] == 'A'
            && bytes[10] == 'V'
            && bytes[11] == 'E') {
            return "wav";
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
