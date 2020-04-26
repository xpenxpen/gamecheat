package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClusterChunk.class);
    
    public List<byte[]> unknowns = new ArrayList<byte[]>();

    @Override
    public void decode(ByteBuffer buffer) {
        
        int count = buffer.getInt();
        LOG.debug("count={}", count);
        
        //TODO not clear
        
        for (int i = 0; i < count; i++) {
            byte[] unknown0 = new byte[350];
            buffer.get(unknown0);
            unknowns.add(unknown0);
        }
    }
    

}
