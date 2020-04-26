package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public class BoundingBoxChunk extends AbstractChunk {
    
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;

    @Override
    public void decode(ByteBuffer buffer) {
        this.minX = buffer.getFloat();
        this.minY = buffer.getFloat();
        this.minZ = buffer.getFloat();
        this.maxX = buffer.getFloat();
        this.maxY = buffer.getFloat();
        this.maxZ = buffer.getFloat();
    }

}
