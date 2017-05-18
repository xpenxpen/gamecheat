package org.xpen.pal.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.XxTea;

public class SmpFile extends CpkFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(SmpFile.class);
    
    
    public SmpFile(String fileName) throws Exception {
        super();
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }

    @Override
    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry._04dwSeek);
            
            byte[] bytes = new byte[fatEntry._05dwLenght1];
            raf.readFully(bytes);
            
            byte[] outBytes;
            if (fatEntry.flag == 0x10005) {
                outBytes = XxTea.decrypt(bytes, CIPHER.getBytes(Charset.forName("ISO-8859-1")));
            } else {
                throw new RuntimeException("Unsupportted flag:" + fatEntry.flag);
            }

            
            String fName = ByteBufferUtil.getNullTerminatedString(raf);

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder, fName);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(outBytes, os);
            os.close();
        }
        
    }

}
