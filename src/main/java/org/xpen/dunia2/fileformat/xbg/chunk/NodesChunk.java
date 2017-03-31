package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
            node.previousSiblingIndex = buffer.getInt();
            node.unknown10 = buffer.getFloat();
            node.unknown14 = buffer.getFloat();
            node.unknown18 = buffer.getFloat();
            node.unknown1C = buffer.getFloat();
            node.unknown20 = buffer.getFloat();
            node.unknown24 = buffer.getFloat();
            node.unknown28 = buffer.getFloat();
            node.unknown2C = buffer.getFloat();
            node.unknown30 = buffer.getFloat();
            node.unknown34 = buffer.getFloat();
            node.o2bMIndex = buffer.getInt();
            node.unknown3C = buffer.getFloat();
            node.unknown40 = buffer.getFloat();

            int length = buffer.getInt();
            byte[] name = new byte[length];
            buffer.get(name);
            node.name = new String(name, Charset.forName("UTF-8"));
            LOG.debug("node.name={}", node.name);
            buffer.get(); // skip null

            items.add(node);
        }
    }
    
    public class Node {
        public int nameHash;
        public int nextSiblingIndex;
        public int firstChildIndex;
        public int previousSiblingIndex;
        public float unknown10;
        public float unknown14;
        public float unknown18;
        public float unknown1C;
        public float unknown20;
        public float unknown24;
        public float unknown28;
        public float unknown2C;
        public float unknown30;
        public float unknown34;
        public int o2bMIndex;
        public float unknown3C;
        public float unknown40;
        public String name;
    }

}
