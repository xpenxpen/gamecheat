package org.xpen.util;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ByteBufferUtil {
    
    public static String getNullTerminatedString(ByteBuffer buffer, String charset) {
        byte[] b = new byte[1];
        buffer.get(b);
        StringBuilder sb = new StringBuilder();
        
        while (b[0]!=0) {
        	if (b[0] < 0) {
        		//handle 2 bytes character like Big5
        		byte[] b2 = new byte[2];
        		b2[0] = b[0];
        		b2[1] = buffer.get();
                sb.append(new String(b2, Charset.forName(charset)));
        	} else {
                sb.append(new String(b, Charset.forName(charset)));
        	}
            buffer.get(b);
        }
        
        return sb.toString();
    }
    
    public static String getNullTerminatedString2Bytes(ByteBuffer buffer, String charset) {
        byte[] b = new byte[2];
        buffer.get(b);
        StringBuilder sb = new StringBuilder();
        
        while (b[0]!=0 || b[1]!=0) {
            sb.append(new String(b, 0, 1, Charset.forName(charset)));
            buffer.get(b);
        }
        
        return sb.toString();
    }
    
    public static String getNullTerminatedString(ByteBuffer buffer) {
        return getNullTerminatedString(buffer, "ISO-8859-1");
    }
    
    public static String getNullTerminatedString2Bytes(ByteBuffer buffer) {
        return getNullTerminatedString2Bytes(buffer, "ISO-8859-1");
    }
    
    public static String getNullTerminatedString(RandomAccessFile raf) throws Exception {
        byte[] b = new byte[1];
        raf.read(b);
        StringBuilder sb = new StringBuilder();
        
        while (b[0]!=0) {
            sb.append(new String(b, Charset.forName("ISO-8859-1")));
            raf.read(b);
        }
        
        return sb.toString();
    }
    
    public static String getFixedLengthString(ByteBuffer buffer, int length) {
        byte[] b = new byte[length];
        buffer.get(b);
        
        return new String(b, Charset.forName("ISO-8859-1"));
    }
    
    public static String getFixedLengthStringXor(ByteBuffer buffer, int length, byte xorKey) {
        byte[] b = new byte[length];
        buffer.get(b);
        for (int i = 0; i < b.length; i++) {
        	b[i] ^= xorKey;
        }
        
        return new String(b, Charset.forName("ISO-8859-1"));
    }
    
    public static String getNullTerminatedFixedLengthString(ByteBuffer buffer, int length) {
        byte[] b = new byte[length];
        buffer.get(b);
        
        return new String(b, 0, length - 1, Charset.forName("ISO-8859-1"));
    }
}
