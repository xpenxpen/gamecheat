package org.xpen.dunia2.fileformat.dat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown"));
        fileHandlersMap.put("bik", new SimpleCopyHandler("bik"));
        fileHandlersMap.put("cseq", new SimpleCopyHandler("cseq"));
        fileHandlersMap.put("fcb", new SimpleCopyHandler("fcb"));
        fileHandlersMap.put("feu", new SimpleCopyHandler("feu"));
        fileHandlersMap.put("lua", new SimpleCopyHandler("lua"));
        fileHandlersMap.put("mat", new SimpleCopyHandler("material.bin"));
        fileHandlersMap.put("png", new SimpleCopyHandler("png"));
        fileHandlersMap.put("root", new SimpleCopyHandler("root.xml"));
        fileHandlersMap.put("spk", new SimpleCopyHandler("spk"));
        fileHandlersMap.put("xbg", new SimpleCopyHandler("xbg"));
        fileHandlersMap.put("xbt", new XbtHandler());
        fileHandlersMap.put("xml", new SimpleCopyHandler("xml"));
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
            if (magic == 0x54414D00 || magic == 0x004D4154) { // '\0MAT'
                return "mat";
            }
        }
        
        if (bytes.length >= 8) {
        	ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.put(bytes, 0, 8);
            buffer.flip();
            long magic = buffer.getLong();
            if (magic == 0x6C6D783F3CBFBBEFL) { //BOM + <?xml
            	return "xml";
            }
            
        }
        
        //ASCII mode
        String text = new String(bytes, Charset.forName("UTF-8"));
        if (text.startsWith("-- {\\v/} Domino auto-generated LUA script file")) {
            return "lua";
        }
        if (text.startsWith("<Root>")) {
            return "root";
        }
        if (text.startsWith("<Sequence>")) {
            return "cseq";
        }
        
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
