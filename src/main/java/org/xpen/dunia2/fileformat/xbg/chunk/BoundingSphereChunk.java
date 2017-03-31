package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public class BoundingSphereChunk extends AbstractChunk {
    
    public float x;
    public float y;
    public float z;
    public float w;

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        this.x = buffer.getFloat();
        this.y = buffer.getFloat();
        this.z = buffer.getFloat();
        this.w = buffer.getFloat();
    }

}
