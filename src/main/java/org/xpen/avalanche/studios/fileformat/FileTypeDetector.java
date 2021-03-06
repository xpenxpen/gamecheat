package org.xpen.avalanche.studios.fileformat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.xpen.avalanche.studios.fileformat.TabFile.FatEntry;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        //fileHandlersMap.put("tpl", new TplHandler("tpl", false));
        fileHandlersMap.put("dds", new SimpleCopyHandler("dds", false));
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
        if (bytes.length >= 4) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.put(bytes, 0, 4);
            buffer.flip();
            int magic = buffer.getInt();
            
            if (magic == 0x20534444) { //DDS
                return "dds";
            }
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
