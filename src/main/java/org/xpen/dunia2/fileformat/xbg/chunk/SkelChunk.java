package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkelChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(SkelChunk.class);
    
    public int unknown00;
    public byte[] bytes;


    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        unknown00 = buffer.getInt();
        LOG.debug("unknown00={}", unknown00);
    }

}
