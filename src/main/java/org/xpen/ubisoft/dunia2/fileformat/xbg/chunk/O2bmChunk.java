package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class O2bmChunk extends AbstractChunk {
    private static final Logger LOG = LoggerFactory.getLogger(O2bmChunk.class);
    
    public List<Matrix> items = new ArrayList<Matrix>();

	@Override
	public void decode(ByteBuffer buffer) {
        int count = buffer.getInt();
        LOG.debug("count={}", count);
        
        for (int i = 0; i < count; i++) {
        	Matrix matrix = new Matrix();
        	matrix.f = new float[16];
        	for (int j = 0; j < 16; j++) {
        		matrix.f[j] = buffer.getFloat();
        	}
            items.add(matrix);
        }

	}
	
    public class Matrix {
        public float[] f;
    }

}
