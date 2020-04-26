package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChunk implements Chunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractChunk.class);
    
    public Chunk parent;
    public List<Chunk> children = new ArrayList<Chunk>();
    
    public int type;

    @Override
    public int getType() {
        return type;
    }
    
    @Override
    public Chunk createChunk(int type) {
        AbstractChunk chunk = null;
        switch (type) {
            case ChunkType.ROOT:
                chunk = this;
                break;
            case ChunkType.MATERIAL_REFERENCE:
                chunk = new MaterialReferenceChunk();
                break;
            case ChunkType.SKEL:
                chunk = new SkelChunk();
                break;
            case ChunkType.NODES:
                chunk = new NodesChunk();
                break;
            case ChunkType.SKID:
                chunk = new SkidChunk();
                break;
            case ChunkType.SKND:
                chunk = new SkndChunk();
                break;
            case ChunkType.CLUSTER:
                chunk = new ClusterChunk();
                break;
            case ChunkType.LODS:
                chunk = new LodChunk();
                break;
            case ChunkType.BOUNDING_BOX:
                chunk = new BoundingBoxChunk();
                break;
            case ChunkType.BOUNDING_SPHERE:
                chunk = new BoundingSphereChunk();
                break;
            case ChunkType.LOD_INFO:
                chunk = new LodInfoChunk();
                break;
            case ChunkType.PCMP:
                chunk = new PcmpChunk();
                break;
            case ChunkType.UCMP:
                chunk = new UcmpChunk();
                break;
            case ChunkType.O2BM:
                chunk = new O2bmChunk();
                break;
            case ChunkType.IKDA:
                chunk = new IkdaChunk();
                break;
        }
        
        if (chunk == null) {
            LOG.warn("Unsupported chunk type:" + type);
            return null;
        }
        
        chunk.type = type;
        return chunk;
    }
    
    public Chunk decodeBlock(ByteBuffer buffer, Chunk parent) {
        int baseOffset = buffer.position();

        int chunkType = buffer.getInt();
        Chunk block = createChunk(chunkType);
        if (chunkType != block.getType()) {
            throw new RuntimeException("chunkType not match");
        }
        
        if (parent!=null) {
            block.setParent(parent);
            parent.addChild(block);
        }
        
        int unknown04 = buffer.getInt();
        int chunkSize = buffer.getInt();
        int dataSize = buffer.getInt();
        int childCount = buffer.getInt();
        
        int childOffset = buffer.position();
        int childEnd = childOffset + (chunkSize - dataSize - 20);
        int blockOffset = childEnd;
        int blockEnd = blockOffset + dataSize;
        
        LOG.debug("type={}, unknown04={}, size={}, dataSize={}, childOffset={}, childEnd={}, blockOffset={}, blockEnd={}",
                block.getClass().getSimpleName(), unknown04, chunkSize, dataSize, childOffset, childEnd, blockOffset, blockEnd);

        if (blockEnd != baseOffset + chunkSize) {
            throw new RuntimeException("blockEnd not match");
        }
        
        for (int i = 0; i < childCount; i++) {
        	Chunk decodeBlock = decodeBlock(buffer, block);
        }
        
        block.decode(buffer);

        if (buffer.position() != blockEnd) {
            LOG.warn("type={}, block not end or exceed, reposition to next chunk={}", block.getClass().getSimpleName(), blockEnd);
            buffer.position(blockEnd);
        }
        
        return block;
    }

    @Override
    public void addChild(Chunk chunk) {
        children.add(chunk);
    }

    @Override
    public void setParent(Chunk chunk) {
        parent = chunk;
    }

    @Override
    public Chunk getParent() {
        return parent;
    }

}
