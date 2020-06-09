package org.xpen.ubisoft.dunia2.fileformat.dat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

public class XbtHandler implements FileTypeHandler {

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        File outFile = null;
        if (!isUnknown) {
                String oldFileNameWithoutExt = newFileName.substring(0, newFileName.lastIndexOf('.'));
                outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + oldFileNameWithoutExt + ".dds");
            
        } else {
            outFile = new File(UserSetting.rootOutputFolder,
            		datFileName + "/unknown/dds/" + newFileName + ".dds");
        }
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        OutputStream os = new FileOutputStream(outFile);
        
        
        //os.write(b, 36, b.length - 36);
        //os.write(b, 0, b.length);
        
        int ddsStartPos = determineDds(b);
        os.write(b, ddsStartPos, b.length - ddsStartPos);
        
        os.close();
    }

	/**
	 * XBT Format
	 * 4 'XBT\0'
	 * 28 unknown
	 * Null-terminated string rounds up to the next word.
	 * - String can be empty, represented by 0000
     * - String is the path to a higher quality mipmap level.
	 */
    private int determineDds(byte[] b) {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int magic = buffer.getInt();
        
        if (magic != 0x00584254) {
            throw new RuntimeException("magic is not XBT");
        }
        int unknown1 = buffer.getInt(); //0x68
        int unknown2 = buffer.getInt(); //position of DDS start
        
        boolean isTrue = true;
        if (isTrue) {
            return unknown2;
        }
        
        int unknown3 = buffer.getInt(); //0x04/    /0x02
        int unknown5 = buffer.getInt(); //01 FF FF FF
        int unknown6 = buffer.getInt(); //79 F8 68 28/ A3 7C 60 E5
        int unknown7 = buffer.getInt(); // 3E 70 36 8F
        int unknown8 = buffer.getInt(); // 5C 3A 74 6C
        
        //above is 32 bytes
        int skipBytes = 4;
        int endOfStr = buffer.getInt();
        while ((endOfStr >> 24) != 0) {
        	skipBytes +=4;
        	endOfStr = buffer.getInt();
        }
        

		return skipBytes + 32;
	}

}
