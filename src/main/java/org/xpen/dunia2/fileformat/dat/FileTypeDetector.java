package org.xpen.dunia2.fileformat.dat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        fileHandlersMap.put("barkdb", new SimpleCopyHandler("barkDataBase.xml", false));
        fileHandlersMap.put("barkmgr", new SimpleCopyHandler("barkManager.xml", false));
        fileHandlersMap.put("bik", new SimpleCopyHandler("bik", true));
        fileHandlersMap.put("capi", new SimpleCopyHandler("capi.txt", false));
        fileHandlersMap.put("cinema", new SimpleCopyHandler("cinemaPostFX.xml", false));
        fileHandlersMap.put("cinv", new SimpleCopyHandler("collectionInventory.xml", false));
        fileHandlersMap.put("cseq", new SimpleCopyHandler("cseq", true));
        fileHandlersMap.put("fcb", new SimpleCopyHandler("fcb", true));
        fileHandlersMap.put("feu", new SimpleCopyHandler("feu", true));
        fileHandlersMap.put("helpmenu", new SimpleCopyHandler("help_menu_topics.xml", false));
        fileHandlersMap.put("lua", new SimpleCopyHandler("lua", true));
        fileHandlersMap.put("mat", new SimpleCopyHandler("material.bin", true));
        fileHandlersMap.put("newpart", new SimpleCopyHandler("newPartLib.xml", false));
        fileHandlersMap.put("oinv", new SimpleCopyHandler("objectInventory.xml", false));
        fileHandlersMap.put("png", new SimpleCopyHandler("png", false));
        fileHandlersMap.put("root", new SimpleCopyHandler("root.xml", false));
        fileHandlersMap.put("spk", new SimpleCopyHandler("spk", true));
        fileHandlersMap.put("stab", new SimpleCopyHandler("stringtable.xml", false));
        fileHandlersMap.put("xbg", new SimpleCopyHandler("xbg", true));
        fileHandlersMap.put("xbt", new XbtHandler());
        fileHandlersMap.put("xml", new SimpleCopyHandler("xml", false));
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
            if (magic == 0x53504B01 || magic == 0x53504B04) { // 'SPK\1' 'SPK\4'
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
        if (text.startsWith("<BarkDataBase>")) {
            return "barkdb";
        }
        if (text.startsWith("<BarkManager>")) {
            return "barkmgr";
        }
        if (text.startsWith("#CAPI_Pawn")) {
            return "capi";
        }
        if (text.startsWith("<CinemaPostFX>")) {
            return "cinema";
        }
        if (text.startsWith("<CollectionInventory>")) {
            return "cinv";
        }
        if (text.startsWith("<help_menu_topics>")) {
            return "helpmenu";
        }
        if (text.startsWith("<NewPartLib>")) {
            return "newpart";
        }
        if (text.startsWith("<ObjectInventory>")) {
            return "oinv";
        }
        if (text.startsWith("<Root>") || text.startsWith("<root>")) {
            return "root";
        }
        if (text.startsWith("<Sequence>")) {
            return "cseq";
        }
        if (text.startsWith("<stringtable")) {
            return "stab";
        }
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
