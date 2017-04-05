package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodesChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(NodesChunk.class);
    
    public int unknown00;
    public byte[] bytes;
    public List<Node> items = new ArrayList<Node>();

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        int count = buffer.getInt();
        
        for (int i = 0; i < count; i++) {
            Node node = new Node();

            node.nameHash = buffer.getInt();
            node.nextSiblingIndex = buffer.getInt();
            node.firstChildIndex = buffer.getInt();
            node.parentId = buffer.getInt();
            node.rotX = buffer.getFloat();
            node.rotY = buffer.getFloat();
            node.rotZ = buffer.getFloat();
            node.rotW = buffer.getFloat();
            node.posX = buffer.getFloat();
            node.posY = buffer.getFloat();
            node.posZ = buffer.getFloat();
            node.scaleX = buffer.getFloat();
            node.scaleY = buffer.getFloat();
            node.scaleZ = buffer.getFloat();
            node.o2bmIndex = buffer.getInt();
            node.unknown3C = buffer.getFloat(); //1.0
            node.unknown40 = buffer.getFloat();

            int length = buffer.getInt();
            byte[] name = new byte[length];
            buffer.get(name);
            node.name = new String(name, Charset.forName("UTF-8"));
            LOG.debug("node.name={}", node.name);
            LOG.debug("node={}", node);
            buffer.get(); // skip null

            items.add(node);
        }
    }
    
    public class Node {
        public int nameHash;
        public int nextSiblingIndex;
        public int firstChildIndex;
        public int parentId;
        
        public float rotX;
        public float rotY;
        public float rotZ;
        public float rotW;
        
        public float posX;
        public float posY;
        public float posZ;
        
        public float scaleX;
        public float scaleY;
        public float scaleZ;
        
        public int o2bmIndex;
        public float unknown3C;
        public float unknown40;
        public String name;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
