package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Position Constant Multiplier
 *
 */
public class PcmpChunk extends AbstractChunk {
    private static final Logger LOG = LoggerFactory.getLogger(PcmpChunk.class);
    
    public float x;
    public float y;

    @Override
    public void decode(ByteBuffer buffer) {
        this.x = buffer.getFloat();
        this.y = buffer.getFloat();
        LOG.debug("x={},y={}",x,y);
        
    }

}
