package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.xbg.chunk.O2bmChunk.Matrix;

public class SkidChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(SkidChunk.class);
    
    public List<Matrix> items = new ArrayList<Matrix>();

    @Override
    public void decode(ByteBuffer buffer) {
        int count = buffer.getInt();
        
        for (int i = 0; i < count; i++) {
        	Matrix matrix = new Matrix();
        	matrix.x = buffer.getInt();
        	matrix.y = buffer.getShort();
        	matrix.z = buffer.getShort();
        	
			if (((RootChunk)parent).majorVer >= 52) {
	        	matrix.w = buffer.getInt();
			}

            items.add(matrix);
        }
    }
    
    public class Matrix {
        public int x;
        public int y;
        public int z;
        public int w;
    }


}
