package org.xpen.pal.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;

public class WavHandler implements FileTypeHandler {
	
	private static final byte[] WAV_HEADER1 = new byte[]{0x52, 0x49, 0x46, 0x46}; //RIFF
	private static final byte[] WAV_HEADER2 = new byte[]{0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20}; //WAVEfmt
	private static final byte[] WAV_HEADER3 = new byte[]{0x64, 0x61, 0x74, 0x61}; //data
    
    private String extension;
    private boolean keepOldFileName;
    private String format;

    public WavHandler(String format, String extension, boolean keepOldFileName) {
        this.format = format;
        this.extension = extension;
        this.keepOldFileName = keepOldFileName;
    }

	@Override
	public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
		File outFile = new File(UserSetting.rootOutputFolder,
        		datFileName + "_" + format + "/wav/" + newFileName + ".wav");

        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        FileOutputStream fos = new FileOutputStream(outFile);
        FileChannel channel = fos.getChannel();
        
        ByteBuffer buffer = ByteBuffer.allocate(b.length + 0x1A);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        
        buffer.put(WAV_HEADER1);
        int wavSize = b.length + 0x12;
        buffer.putInt(wavSize);
        buffer.put(WAV_HEADER2);
        buffer.putInt(0x10);  //fmt chunk size
        buffer.put(b, 0, 16);
        buffer.put(WAV_HEADER3);
        buffer.putInt(b.length - 0x12);  //data chunk size
        buffer.put(b, 0x12, b.length - 0x12);
        
        buffer.flip();
        
        channel.write(buffer);
        
        channel.close();
        fos.close();
 	}

}
