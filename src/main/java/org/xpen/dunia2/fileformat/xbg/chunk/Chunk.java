package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

public interface Chunk extends ChunkFactory {
    
    int getType();
    
    void decode(ByteBuffer buffer, Chunk chunk);

    void addChild(Chunk chunk);
}
