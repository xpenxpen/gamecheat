package org.xpen.util.compress;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Nintendo LZ-0x10
 * Data header (32bit)
 *   Bit 0-3   Reserved
 *   Bit 4-7   Compressed type (must be 1 for LZ77)
 *   Bit 8-31  Size of decompressed data
 * Repeat below. Each Flag Byte followed by eight Blocks.
 * Flag data (8bit)
 *   Bit 0-7   Type Flags for next 8 Blocks, MSB first
 * Block Type 0 - Uncompressed - Copy 1 Byte from Source to Dest
 *   Bit 0-7   One data byte to be copied to dest
 * Block Type 1 - Compressed - Copy N+3 Bytes from Dest-Disp-1 to Dest
 *   Bit 0-3   Disp MSBs
 *   Bit 4-7   Number of bytes to copy (minus 3)
 *   Bit 8-15  Disp LSBs
 *
 */
public class NintendoLz10Compressor {
    
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
            int bitWise = 128;
            for (byte round = 0; round < 8; round++) {
                if (!buffer.hasRemaining()) {
                    break;
                }
                if ((symbol & bitWise) != 0) {
                    byte b1 = buffer.get();
                    byte b2 = buffer.get();
                    //System.out.println("pos:"+buffer.position());
                    //if (buffer.position()>=5808){
                    //    return outByte;
                    //}
                    int bytesToCopy = ((b1 & 0xFF) >> 4) + 3;
                    int disp = ((b1 & 0x0F) << 8) + (b2 & 0xFF) + 1;
                    //System.out.println("bytesToCopy="+bytesToCopy+",disp="+disp);
                    
                    int newBlockStartPos = outFileCurrentPosition - disp;
                    int newBlockLimitPos = outFileCurrentPosition;
                    int newBlockCurrentPos = newBlockStartPos;
                    while (bytesToCopy > 0) {
                        if (newBlockCurrentPos == newBlockLimitPos) {
                            newBlockCurrentPos = newBlockStartPos;
                        }
                        outByte[outFileCurrentPosition] = outByte[newBlockCurrentPos];
                        //System.out.println(Integer.toHexString(0xFF & outByte[outFileCurrentPosition]));
                        bytesToCopy--;
                        
                        newBlockCurrentPos++;
                        outFileCurrentPosition++;
                    }
                    //System.out.println("---");
                    
                } else {
                    outByte[outFileCurrentPosition] = buffer.get();
                    //System.out.println(Integer.toHexString(0xFF & outByte[outFileCurrentPosition]));
                    outFileCurrentPosition++;
                }
                bitWise /= 2;
            }
        }
        return outByte;
    }

}
