package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkndChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(SkndChunk.class);
    
    public int unknown00;
    public byte[] bytes;
    public List<UnknownData0> items = new ArrayList<UnknownData0>();
    public List<ClusterChunk> clusters = new ArrayList<ClusterChunk>();


    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        int count = buffer.getInt();
        LOG.debug("count={}", count);
        
        for (int i = 0; i < count; i++) {
            UnknownData0 node = new UnknownData0();

            node.unknown00 = buffer.getInt();
            node.unknown04 = buffer.getInt();
            node.unknown08 = buffer.getInt();
            node.unknown0C = buffer.getInt();
            node.unknown10 = buffer.getInt();
            node.unknown14 = buffer.getInt();
            node.unknown18 = buffer.getInt();
            node.unknown1C = buffer.getInt();
            node.unknown20 = buffer.getInt();
            node.unknown24 = buffer.getInt();
            node.unknown28 = buffer.getInt();
            node.unknown2C = buffer.getInt();
            node.unknown30 = buffer.getInt();

            int length = buffer.getInt();
            byte[] name = new byte[length];
            buffer.get(name);
            node.name = new String(name, Charset.forName("UTF-8"));
            LOG.debug("node.name={}", node.name);
            buffer.get(); // skip null

            items.add(node);
        }
    }

    
    public class UnknownData0 {
        public float unknown00;
        public float unknown04;
        public float unknown08;
        public float unknown0C;
        public float unknown10;
        public float unknown14;
        public float unknown18;
        public float unknown1C;
        public float unknown20;
        public float unknown24;
        public float unknown28;
        public int unknown2C;
        public int unknown30;
        public String name;
    }

}
