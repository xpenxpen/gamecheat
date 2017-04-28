package org.xpen.util;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ByteBufferUtil {
    
    public static String getNullTerminatedString(ByteBuffer buffer) {
        byte[] b = new byte[1];
        buffer.get(b);
        StringBuilder sb = new StringBuilder();
        
        while (b[0]!=0) {
            sb.append(new String(b, Charset.forName("ISO-8859-1")));
            buffer.get(b);
        }
        
        return sb.toString();
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
    
    public static String getNullTerminatedFixedLengthString(ByteBuffer buffer, int length) {
        byte[] b = new byte[length];
        buffer.get(b);
        
        return new String(b, 0, length - 1, Charset.forName("ISO-8859-1"));
    }
}
