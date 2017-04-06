package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkelChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(SkelChunk.class);
    
    public int hasSkeleton;


    @Override
    public void decode(ByteBuffer buffer) {
    	hasSkeleton = buffer.getInt();
        LOG.debug("hasSkeleton={}", hasSkeleton);
    }

}
