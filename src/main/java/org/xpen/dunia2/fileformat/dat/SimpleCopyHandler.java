package org.xpen.dunia2.fileformat.dat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.xpen.farcry3.UserSetting;

public class SimpleCopyHandler implements FileTypeHandler {
    
    private String extension;

    public SimpleCopyHandler(String extension) {
        this.extension = extension;
    }

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        File outFile = null;
        if (!isUnknown) {
            if (!extension.equals("unknown")) {
            	String oldFileNameWithoutExt = null;
            	if (newFileName.lastIndexOf('.') == -1) {
            		oldFileNameWithoutExt = newFileName;
            	} else {
                    oldFileNameWithoutExt = newFileName.substring(0, newFileName.lastIndexOf('.'));
            	}
                outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + oldFileNameWithoutExt + "." + extension);
            } else {
                outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + newFileName);
            }
            
        } else {
            outFile = new File(UserSetting.rootOutputFolder,
            		datFileName + "/unknown/" + extension + "/" + newFileName + "." + extension);
        }
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        OutputStream os = new FileOutputStream(outFile);
        
        IOUtils.write(b, os);
        os.close();
        
    }

}