package org.xpen.dunia2.fileformat.dat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.xpen.farcry3.UserSetting;

public class XbtHandler implements FileTypeHandler {

    @Override
    public void handle(byte[] b, String newFileName, boolean isUnknown) throws Exception {
        File outFile = null;
        if (!isUnknown) {
                String oldFileNameWithoutExt = newFileName.substring(0, newFileName.lastIndexOf('.'));
                outFile = new File(UserSetting.rootOutputFolder, oldFileNameWithoutExt + ".dds");
            
        } else {
            outFile = new File(UserSetting.rootOutputFolder, "unknown/" + newFileName + ".dds");
        }
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        OutputStream os = new FileOutputStream(outFile);
        
        os.write(b, 36, b.length - 36);
        os.close();
    }

}
