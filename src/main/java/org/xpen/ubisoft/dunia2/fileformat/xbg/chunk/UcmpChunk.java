package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UV Constant Multiplier
 *
 */
public class UcmpChunk extends AbstractChunk {
    private static final Logger LOG = LoggerFactory.getLogger(UcmpChunk.class);

    public float u;
    public float v;

    @Override
    public void decode(ByteBuffer buffer) {
        this.u = buffer.getFloat();
        this.v = buffer.getFloat();
        LOG.debug("u={},v={}",u,v);
    }

}
