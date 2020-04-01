package org.xpen.util.compress;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Nintendo RLE
 *    Data header (32bit)
 *      Bit 0-3   Reserved
 *      Bit 4-7   Compressed type (must be 3 for run-length)
 *      Bit 8-31  Size of decompressed data
 *    Repeat below. Each Flag Byte followed by one or more Data Bytes.
 *    Flag data (8bit)
 *      Bit 0-6   Expanded Length / Copy Count (uncompressed N-1, compressed N-3)
 *                (If the Flag is 0 this specifies how many bytes after the flag are copied.
 *                Otherwise it specifies how many times the next byte is copied. )
 *      Bit 7     Flag (0=uncompressed, 1=compressed)
 *    Data Byte(s) - N uncompressed bytes, or 1 byte repeated N times
 */
public class NintendoRleCompressor {
    
    public static byte[] decompress(byte[] inByte) {
        ByteBuffer buffer = ByteBuffer.wrap(inByte);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte magic = buffer.get();
        byte[] sizeBytes = new byte[3];
        buffer.get(sizeBytes);
        int decompressSize = ((sizeBytes[2] & 0xFF) << 16) + ((sizeBytes[1] & 0xFF) << 8) + (sizeBytes[0] & 0xFF);
        
        int outFileCurrentPosition = 0;
        
        byte[] outByte = new byte[decompressSize];
        while (buffer.hasRemaining()) {
            byte symbol = buffer.get();
            boolean compressed = false;
            if ((symbol & 0x80) != 0) {
                compressed = true;
            }
            int runLength = (symbol & 0x7F);

            if (compressed) {
                byte inb = buffer.get();
                for (int i = 0; i < runLength + 3; i++) {
                    outByte[outFileCurrentPosition] = inb;
                    outFileCurrentPosition++;
                }
                
            } else {
                byte[] ins = new byte[runLength + 1];
                buffer.get(ins);
                System.arraycopy(ins, 0, outByte, outFileCurrentPosition, runLength + 1);
                outFileCurrentPosition = outFileCurrentPosition + runLength + 1;
            }
        }
        return outByte;
    }

}
