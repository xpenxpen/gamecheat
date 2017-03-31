package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public class LodInfoChunk extends AbstractChunk {
    
    public int count;
    public int unknown1;

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        this.count = buffer.getInt();
        this.unknown1 = buffer.getInt();
    }

}
