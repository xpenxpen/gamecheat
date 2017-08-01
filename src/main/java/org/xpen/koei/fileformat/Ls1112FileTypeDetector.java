package org.xpen.koei.fileformat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.koei.fileformat.Ls1112.FatEntry;

public class Ls1112FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", false));
        fileHandlersMap.put("bmp", new SimpleCopyHandler("bmp", false));
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
    	
        if (bytes.length >= 6
	        && bytes[0] == 'B'
	        && bytes[1] == 'M') {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(2);
            int fileSize = buffer.getInt();
            buffer.clear();
            if (fileSize == bytes.length) {
                return "bmp";
            }
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
