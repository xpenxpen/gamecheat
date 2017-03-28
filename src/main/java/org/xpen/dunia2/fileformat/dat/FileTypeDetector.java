package org.xpen.dunia2.fileformat.dat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown"));
        fileHandlersMap.put("fcb", new SimpleCopyHandler("fcb"));
        fileHandlersMap.put("png", new SimpleCopyHandler("png"));
        fileHandlersMap.put("xbt", new XbtHandler());
    }
    
    public static String detect(byte[] bytes) {
        if (bytes.length >= 5
         && bytes[0] == 'M'
         && bytes[1] == 'A'
         && bytes[2] == 'G'
         && bytes[3] == 'M'
         && bytes[4] == 'A') {
            return "mgb";
        }
        
        if (bytes.length >= 3
                && bytes[0] == 'B'
                && bytes[1] == 'I'
                && bytes[2] == 'K') {
                   return "bik";
               }
        
        if (bytes.length >= 3
                && bytes[0] == 'U'
                && bytes[1] == 'E'
                && bytes[2] == 'F') {
                   return "feu";
               }
        
        if (bytes.length >= 4) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.put(bytes, 0, 4);
            buffer.flip();
            int magic = buffer.getInt();
            
            if (magic == 0x474E5089) {
                return "png";
            }
            if (magic == 0x00584254 || magic == 0x54425800) {
                return "xbt";
            }
            if (magic == 0x53504801) { // 'SPK\1'
                return "spk";
            }
            if (magic == 0x4D455348) { // 'MESH'
                return "xbg";
            }
            if (magic == 0x4643626E) { // 'FCbn'
                return "fcb";
            }
        }
        
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
