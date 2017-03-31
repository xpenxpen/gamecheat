package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LodChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(LodChunk.class);
    
    public List<LevelOfDetail> lods = new ArrayList<LevelOfDetail>();
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public void decode(ByteBuffer buffer, Chunk parent) {
        
        int count = buffer.getInt();
        LOG.debug("count={}, pos={}", count, buffer.position());
        
        for (int i = 0; i < count; i++) {
            LevelOfDetail node = new LevelOfDetail();
            node.decode(buffer);
            lods.add(node);
        }
        LOG.debug("lods={}", lods);
    }
    
    public class LevelOfDetail {
        public float unknown0; // seems to be a distance value for determining which LOD to use
        public List<VertexBuffer> vbs = new ArrayList<VertexBuffer>();
        public List<Primitive> primitives = new ArrayList<Primitive>();
        public byte[] vertexData;
        public short[] indices;
        
        public void decode(ByteBuffer buffer) {
            this.unknown0 = buffer.getFloat();
            int vbCount = buffer.getInt();
            LOG.debug("vertex buffer Count={}, pos={}", vbCount, buffer.position());
            
            for (int j = 0; j < vbCount; j++) {
                VertexBuffer dataInfo = new VertexBuffer();
                dataInfo.format = buffer.getInt();
                dataInfo.size = buffer.getInt();
                dataInfo.count = buffer.getInt();
                dataInfo.offset = buffer.getInt();
                this.vbs.add(dataInfo);
            }   
            
            int primitiveCount = buffer.getInt();
            LOG.debug("primitiveCount={}, pos={}", primitiveCount, buffer.position());
            for (int j = 0; j < primitiveCount; j++) {
                Primitive primitive = new Primitive();
                primitive.bufferIndex = buffer.getInt();
                primitive.skeletonIndex = buffer.getInt();
                primitive.materialIndex = buffer.getInt();
                primitive.indicesStartIndex = buffer.getInt();
                primitive.unknown4 = buffer.getInt();
                primitive.unknown5 = buffer.getInt();
                primitive.unknown6 = buffer.getInt();
                this.primitives.add(primitive);
            }
            
            int vertexDataSize = buffer.getInt();
            LOG.debug("vertexDataSize={}, pos={}", vertexDataSize, buffer.position());
            int curPos = buffer.position();
            // data is aligned to 16 bytes, ugh
            if (curPos % 16 != 0) {
                int skip = 16 - (curPos % 16);
                buffer.position(curPos + skip);
            }
            //input.Seek(input.Position.Align(16), SeekOrigin.Begin);
            this.vertexData = new byte[vertexDataSize];
            buffer.get(this.vertexData);
            

            int indexCount = buffer.getInt();
            LOG.debug("indexCount={}, pos={}", indexCount, buffer.position());
            // data is aligned to 16 bytes, ugh
            // data is aligned to 16 bytes, ugh
            curPos = buffer.position();
            if (curPos % 16 != 0) {
                int skip = 16 - (curPos % 16);
                buffer.position(curPos + skip);
            }
            //input.Seek(input.Position.Align(16), SeekOrigin.Begin);
            this.indices = new short[indexCount];
            for (int i = 0; i < indexCount; i++) {
                this.indices[i] = buffer.getShort();
            }
        }
    }
    
    public class VertexBuffer {
        public int format;
        public int size;
        public int count;
        public int offset;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    public class Primitive {
        public int bufferIndex;
        public int skeletonIndex;
        public int materialIndex;
        public int indicesStartIndex;
        public int unknown4;
        public int unknown5;
        public int unknown6;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
