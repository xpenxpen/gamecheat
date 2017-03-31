package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public class UcmpChunk extends AbstractChunk {

    public float x;
    public float y;

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        this.x = buffer.getFloat();
        this.y = buffer.getFloat();
    }

}
