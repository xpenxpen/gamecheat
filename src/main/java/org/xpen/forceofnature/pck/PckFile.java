package org.xpen.forceofnature.pck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.farcry3.UserSetting;

public class PckFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PckFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private int version;
    private int platform;
    private String fileName;
    
    private List<Entry> entries = new ArrayList<Entry>();
    
    public PckFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".pck"), "r");
        fileChannel = raf.getChannel();
    }
    
    public void decode() throws Exception {
    	decodeFat();
    	decodeDat();
    }

	/**
     *
     */
    public void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        ByteBuffer newBuffer = ByteBuffer.allocate(100);
        newBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        ByteBuffer newTempBuffer = ByteBuffer.allocate(100);
        newTempBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int entryCount = buffer.getInt();
        
        int pos = 0;
        int length = 100;
        
        buffer.limit(length-pos);
        buffer.rewind();
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0; i < entryCount; i++) {
            
            newBuffer.limit(100);
            newBuffer.rewind();
            newBuffer.put(buffer);
            if (pos!=0) {
                newTempBuffer.limit(pos);
                newTempBuffer.rewind();
                fileChannel.read(newTempBuffer);
                newTempBuffer.flip();
                newBuffer.put(newTempBuffer);
            }
            newBuffer.flip();
           
            byte[] b = new byte[1];
            newBuffer.get(b);
            StringBuilder sb = new StringBuilder();
            
            while (b[0]!=0) {
                sb.append(new String(b, Charset.forName("ISO-8859-1")));
                newBuffer.get(b);
            }
            
            Entry entry = new Entry();
            entry.start = newBuffer.getLong();
            entry.length = newBuffer.getLong();
            entry.fileName = sb.toString();
            entries.add(entry);
            
            pos = newBuffer.position();
            buffer.limit(100);
            buffer.rewind();
            buffer.put(newBuffer);
            buffer.flip();
            
            //copy rest byte buffer for next loop
        }
        buffer.clear();
        
    }
    
    private void decodeDat() throws Exception {
        for (Entry entry : entries) {
            LOG.debug(entry.toString());
            raf.seek(entry.start);
    		byte[] b = new byte[(int)entry.length];
        	raf.readFully(b);
        	
        	File outFile = new File(UserSetting.rootOutputFolder+"/"+fileName, entry.fileName);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(b, os);
            os.close();
        }
	}

	public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
    public List<Entry> getEntries() {
        return entries;
    }

}
