package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaterialReferenceChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(MaterialReferenceChunk.class);
    
    public int unknown00;
    public Map<String, String> files = new HashMap<String, String>();


    @Override
    public void decode(ByteBuffer buffer) {
    	
    	if (parent.getType() != ChunkType.ROOT) {
    		throw new RuntimeException("MaterialReferenceChunk must be child of RootChunk");
    	}
    	
    	RootChunk rootChunk = (RootChunk)parent;
    	
        int count = buffer.getInt();
        
        LOG.debug("rootChunk.majorVer={}", rootChunk.majorVer);

        if (rootChunk.majorVer >= 52) {
            unknown00 = buffer.getInt();
        }
        
        for (int i = 0; i < count; i++) {
            int length1 = buffer.getInt();
            byte[] fileName = new byte[length1];
            buffer.get(fileName);
            buffer.get(); // skip null
            
            if (rootChunk.majorVer >= 52) {
	
	            int length2 = buffer.getInt();
	            byte[] key = new byte[length2];
	            buffer.get(key);
	            buffer.get(); // skip null
	            
	            files.put(new String(key, Charset.forName("UTF-8")), new String(fileName, Charset.forName("UTF-8")));
            }

        }
        
        LOG.debug("files={}", files);
        
        

    }


}
