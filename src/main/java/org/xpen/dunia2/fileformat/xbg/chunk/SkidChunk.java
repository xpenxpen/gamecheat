package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.xbg.chunk.NodesChunk.Node;

public class SkidChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(SkidChunk.class);
    
    public List<byte[]> unknowns = new ArrayList<byte[]>();

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        int count = buffer.getInt();
        
        for (int i = 0; i < count; i++) {
            byte[] unknown = new byte[12];
            buffer.get(unknown);

            unknowns.add(unknown);
        }
    }


}
