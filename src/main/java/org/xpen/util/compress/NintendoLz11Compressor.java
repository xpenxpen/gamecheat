package org.xpen.util.compress;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Nintendo LZ-0x11
 * Data header (32bit)
 *   Bit 0-3   Reserved
 *   Bit 4-7   Compressed type (must be 1 for LZ77)
 *   Bit 8-31  Size of decompressed data
 * Repeat below. Each Flag Byte followed by eight Blocks.
 * Flag data (8bit)
 *   Bit 0-7   Type Flags for next 8 Blocks, MSB first
 * Block Type 0 - Uncompressed - Copy 1 Byte from Source to Dest
 *   Bit 0-7   One data byte to be copied to dest
 * Block Type 1 - Compressed - Copy LEN Bytes from Dest-Disp-1 to Dest
 * 
 *   If Reserved is 0: - Default(Same as LZ10)
 *     Bit 0-3   Disp MSBs
 *     Bit 4-7   LEN - 3
 *     Bit 8-15  Disp LSBs
 *   If Reserved is 1: - Higher compression rates for files with (lots of) long repetitions
 *     Bit 4-7   Indicator
 *        If Indicator > 1:
 *            Bit 0-3    Disp MSBs
 *            Bit 4-7    LEN - 1 (same bits as Indicator)
 *            Bit 8-15   Disp LSBs
 *        If Indicator is 1: A(B CD E)(F GH)
 *            Bit 0-3     (LEN - 0x111) MSBs
 *            Bit 4-7     Indicator; unused
 *            Bit 8-15    (LEN- 0x111) 'middle'-SBs
 *            Bit 16-19   Disp MSBs
 *            Bit 20-23   (LEN - 0x111) LSBs
 *            Bit 24-31   Disp LSBs
 *        If Indicator is 0:
 *            Bit 0-3     (LEN - 0x11) MSBs
 *            Bit 4-7     Indicator; unused
 *            Bit 8-11    Disp MSBs
 *            Bit 12-15   (LEN - 0x11) LSBs
 *            Bit 16-23   Disp LSBs
 */
public class NintendoLz11Compressor {
    
    public static byte[] decompress(byte[] inByte) {
        ByteBuffer buffer = ByteBuffer.wrap(inByte);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte magic = buffer.get();
        if (magic != 0x11) {
            throw new RuntimeException("Not Lz11, magic=" + magic);
        }
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
                    byte b3;
                    byte b4;
                    
                    int bytesToCopy;
                    int disp;
                    
                    int indicator = ((b1 & 0xF0) >> 4);
                    if (indicator == 1) {
                        b3 = buffer.get();
                        b4 = buffer.get();
                        //LEN:AAAA+BBBBBBBB+CCCC
                        bytesToCopy = ((b1 & 0x0F) << 12) + ((b2 & 0xFF) << 4) + ((b3 & 0xF0) >> 4) + 0x111;
                        disp = ((b3 & 0x0F) << 8) + (b4 & 0xFF) + 1;
                    } else if  (indicator == 0) {
                        b3 = buffer.get();
                        //LEN:AAAA+BBBB
                        bytesToCopy = ((b1 & 0x0F) << 4) + ((b2 & 0xF0) >> 4) + 0x11;
                        disp = ((b2 & 0x0F) << 8) + (b3 & 0xFF) + 1;
                    } else {
                        bytesToCopy = ((b1 & 0xF0) >> 4) + 1;
                        disp = ((b1 & 0x0F) << 8) + (b2 & 0xFF) + 1;
                    }
                    
                    //System.out.println("pos:"+buffer.position());
                    //if (buffer.position()>=5808){
                    //    return outByte;
                    //}
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
