package org.xpen.util;

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
}
