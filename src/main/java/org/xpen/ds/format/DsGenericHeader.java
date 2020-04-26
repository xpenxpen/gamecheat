package org.xpen.ds.format;

import java.nio.ByteBuffer;

import org.xpen.util.ByteBufferUtil;

public class DsGenericHeader {
    public String magic;
    public int constant; //Always 0xFFFE0001
    public int sectionSize;
    public short headerSize; //Always 0x10
    public short sectionCount; //Number of sections
    
    public void decode(ByteBuffer buffer) throws Exception {
        magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
        constant = buffer.getInt();
        sectionSize = buffer.getInt();
        headerSize = buffer.getShort();
        sectionCount = buffer.getShort();
    }

}
