package org.xpen.aquaplus.fileformat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.xpen.aquaplus.fileformat.DarFile.FatEntry;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        fileHandlersMap.put("tpl", new TplHandler("tpl", false));
        fileHandlersMap.put("wav", new SimpleCopyHandler("wav", false));
    }
    
    public static String detect(FatEntry entry, byte[] bytes) {
        if (entry.fname.endsWith(".tpl")) {
        	if (bytes.length >= 20) {
                ByteBuffer buffer = ByteBuffer.allocate(20);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.put(bytes, 0, 20);
                buffer.flip();
                int magic = buffer.getInt();
                if (magic != 1) {
                	return "unknown";
                }
                if (buffer.getInt() != 8) {
                	return "unknown";
                }
                if (buffer.getInt() != 0x10) {
                	return "unknown";
                }
                if (buffer.getInt() != 0x24) {
                	return "unknown";
                }
                //TODO can only handle 480*360
                if (buffer.getShort() != 360) {
                	return "unknown";
                }
                if (buffer.getShort() != 480) {
                	return "unknown";
                }
                return "tpl";
        	} else {
        		return "unknown";
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
