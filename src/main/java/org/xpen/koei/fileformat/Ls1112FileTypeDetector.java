package org.xpen.koei.fileformat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.xpen.koei.fileformat.Ls1112.FatEntry;
import org.xpen.koei.sangokushi.fileformat.E5Handler;
import org.xpen.koei.sangokushi.fileformat.R3Handler;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class Ls1112FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", false));
        fileHandlersMap.put("bmp", new SimpleCopyHandler("bmp", false));
        fileHandlersMap.put("wav", new SimpleCopyHandler("wav", false));
        //fileHandlersMap.put("r3", new R3Handler());
        fileHandlersMap.put("e5", new E5Handler());
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
        
        if (entry.gameName.equals(Ls1112.GAME_NAME_YJZ)) {
            return "r3";
        }
        if (entry.gameName.equals(Ls1112.GAME_NAME_CCZ)) {
            return "e5";
        }
    	
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
