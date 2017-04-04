package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public class UcmpChunk extends AbstractChunk {

    public float u;
    public float v;

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        this.u = buffer.getFloat();
        this.v = buffer.getFloat();
    }

}
