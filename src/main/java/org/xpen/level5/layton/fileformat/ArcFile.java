package org.xpen.level5.layton.fileformat;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.compress.NintendoLz10Compressor;
import org.xpen.util.compress.NintendoRleCompressor;
import org.xpen.util.handler.FileTypeHandler;

/**
 * 4 Compression Type
 * Next all is compressed data
 * After decompress, decode animation
 *
 */
public class ArcFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArcFile.class);
    
    public Map<String, FileTypeHandler> supporttedTypes = new HashMap<>();
    
    public void addFolderType(String folderName, FileTypeHandler fileTypeHandler) {
        supporttedTypes.put(folderName, fileTypeHandler);
    }
    
    public void decode(String folderName, File f) throws Exception {
        byte[] inBytes = FileUtils.readFileToByteArray(f);
        byte[] outBytes = null;
        
        //Support type 01/02 now
        if (inBytes[0] == 2 && inBytes[1] == 0 && inBytes[2] == 0 && inBytes[3] == 0) {
            inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
            outBytes = NintendoLz10Compressor.decompress(inBytes);
        } else if (inBytes[0] == 1 && inBytes[1] == 0 && inBytes[2] == 0 && inBytes[3] == 0) {
            inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
            outBytes = NintendoRleCompressor.decompress(inBytes);
        } else {
            LOG.warn("Unknown compress type, skip {}", f.getName());
            throw new RuntimeException("Unknown compress type");
        }
        
        
        //FileUtils.writeByteArrayToFile(new File("D:/git/opensource/gamecheat/1.dat"), outBytes);
        //break;
        
        handle(folderName, f, outBytes);
    }

    /**
     * @param folderName
     * @param f
     * @param outBytes
     * @throws Exception
     */
    protected void handle(String folderName, File f, byte[] outBytes) throws Exception {
        String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
        FileTypeHandler handler = supporttedTypes.get(folderName);
        File parent = f.getParentFile();
        Stack<String> folderNameStack = new Stack<>();
        while (!parent.getName().equals(folderName)) {
            folderNameStack.push(parent.getName());
            parent = parent.getParentFile();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(folderName);
        while (!folderNameStack.isEmpty()) {
            sb.append("/");
            sb.append(folderNameStack.pop());
        }
        handler.handle(outBytes, sb.toString(), oldFileNameWithoutExt, false);
    }
}
