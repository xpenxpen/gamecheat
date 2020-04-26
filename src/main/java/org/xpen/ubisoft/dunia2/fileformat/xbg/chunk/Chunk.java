package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public interface Chunk extends ChunkFactory {
    
    int getType();
    
    void decode(ByteBuffer buffer);

    void addChild(Chunk chunk);
    
    void setParent(Chunk chunk);
    
    Chunk getParent();
}
