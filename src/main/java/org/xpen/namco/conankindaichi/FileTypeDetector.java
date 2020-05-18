package org.xpen.namco.conankindaichi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;

import com.google.common.collect.Sets;

public class FileTypeDetector {
    
    private static Map<String, FileTypeHandler> fileHandlersMap = new HashMap<String, FileTypeHandler>();
    private static Set<String> BACKGROUD_512 = Sets.newHashSet(
            "005", "006", "046", "068", "081", "082", "087", "088", "089", "090",
            "124", "125", "145", "163", "190", "199",
            "478", "479", "480", "481", "482", "483", "484", "485", "486", "487", "488", "489",
            "490", "491", "492", "493", "494", "495", "516", "517", "565", "567", "598");
    
    static {
        fileHandlersMap.put("unknown", new SimpleCopyHandler("unknown", true));
        fileHandlersMap.put("img64", new ImgHandler(32, 64));
        fileHandlersMap.put("img256", new ImgHandler(256, 256));
        fileHandlersMap.put("img512", new ImgHandler(256, 512));
        fileHandlersMap.put("img768", new ImgHandler(256, 768));
        fileHandlersMap.put("charaimg64", new CharaImgHandler());
    }
    
    public static String detect(String fileName, String fname) {
        if (fileName.equals("all_info_main")) {
        	return "img64";
        }
        if (fileName.equals("talk_char")) {
            return "img64";
        }
        if (fileName.equals("background")) {
            if (fname.equals("002")) {
                return "img768";
            }
            if (BACKGROUD_512.contains(fname)) {
                return "img512";
            }
            return "img256";
        }
        if (fileName.equals("chara")) {
            return "charaimg64";
        }
        
        return "unknown";
    }
    
    public static FileTypeHandler getFileTypeHandler(String type) {
        return fileHandlersMap.get(type);
    }

}
