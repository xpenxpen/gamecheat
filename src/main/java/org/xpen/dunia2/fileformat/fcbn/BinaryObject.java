package org.xpen.dunia2.fileformat.fcbn;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryObject {
    
    private static final Logger LOG = LoggerFactory.getLogger(BinaryObject.class);
    
    public int level;
    public long position;
    public int nameHash; //TODO this is CRC32
    public List<Pair<Integer, byte[]>> fields = new ArrayList<Pair<Integer, byte[]>>();
    public List<BinaryObject> children = new ArrayList<BinaryObject>();
    
    public void decode(ByteBuffer buffer) {
        //child count
        Pair<Integer, Boolean> childPair = BinaryObject.getCount(buffer);
        
        if (childPair.getRight()) {
            return;
        }
        
        //name hash
        this.nameHash = buffer.getInt();
        
        //field count
        Pair<Integer, Boolean> fieldPair = BinaryObject.getCount(buffer);
        
        
//        if (pair.getRight()) {
//            throw new UnsupportedOperationException();
//        }
        
        //LOOP field
        LOG.debug("fieldPair count={}", fieldPair.getLeft());
        for (int i = 0, n = fieldPair.getLeft(); i<n; i++) {
            LOG.debug("position={}", Integer.toHexString(buffer.position()));
            int nameHash = buffer.getInt();
            Pair<Integer, Boolean> pairInner = BinaryObject.getCount(buffer);
            if (pairInner.getRight()) {
                //throw new UnsupportedOperationException();
                //BinaryObject.getCount(buffer);
                this.fields.add(new ImmutablePair<Integer, byte[]>(nameHash, new byte[]{}));
            } else {
                LOG.debug("before getting bytes,pos={},count={}", Integer.toHexString(buffer.position()), pairInner.getLeft());
                byte[] bytes = new byte[pairInner.getLeft()];
                buffer.get(bytes);
                LOG.debug("after getting bytes,pos={}", Integer.toHexString(buffer.position()));
                this.fields.add(new ImmutablePair<Integer, byte[]>(nameHash, bytes));
            }
        }
        
        LOG.debug("children loop start, level={}, pos={}", level, Integer.toHexString(buffer.position()));
        
        //LOOP child
        for (int i = 0; i < childPair.getLeft(); i++) {
            BinaryObject bo = new BinaryObject();
            bo.level = this.level+1;
            bo.decode(buffer);
            this.children.add(bo);
        }
        LOG.debug("children loop end, level={}, pos={}", level, Integer.toHexString(buffer.position()));



    }
    
    public static Pair<Integer, Boolean> getCount(ByteBuffer buffer) {
        LOG.debug("getCount, position={}", Integer.toHexString(buffer.position()));
        byte readValue = buffer.get();
        int count = readValue;
        boolean isOffset = false;
        
        if (count < 0) {
            count+=256;
        }

        if (count < 0xFE) {
            
        } else {
            if  (count == 0xFE) {
                isOffset = true;
            }
            count = buffer.getInt();
        }
        
        LOG.debug("count={}, isOffset={}", count, isOffset);
        return new ImmutablePair<Integer, Boolean>(count, isOffset);
    }
    
    public void dump2Xml(StringBuilder sb) {
        sb.append(StringUtils.repeat(' ', 4*level));
        sb.append("<object hash=\"");
        sb.append(Integer.toHexString(nameHash));
        sb.append("\">\n");
        
        for (Pair<Integer, byte[]> field : fields) {
            sb.append(StringUtils.repeat(' ', 4*level+4));
            sb.append("<field hash=\"");
            sb.append(Integer.toHexString(field.getLeft()));
            sb.append("\">");
            
            byte[] right = field.getRight();
            if (right.length >= 2 && (right[right.length-1]==0) && (right[right.length-2]!=0)) {
                //treat as string
                sb.append(new String(right, Charset.forName("ISO-8859-1")));
            } else {
                sb.append(Hex.encodeHexString(right));
            }
            sb.append("</field>\n");
        }
        
        for (BinaryObject bo : children) {
            bo.dump2Xml(sb);
        }
        
        sb.append(StringUtils.repeat(' ', 4*level));
        sb.append("</object>\n");
    }

}
