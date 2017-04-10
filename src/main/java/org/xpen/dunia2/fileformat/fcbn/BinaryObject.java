package org.xpen.dunia2.fileformat.fcbn;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class BinaryObject {
    public long position;
    public int nameHash;
    public Map<Integer, byte[]> fields = new HashMap<Integer, byte[]>();
    public List<BinaryObject> children = new ArrayList<BinaryObject>();
    
    public void decode(ByteBuffer buffer, int childCount) {
        this.nameHash = buffer.getInt();
        Pair<Integer, Boolean> pair = BinaryObject.getCount(buffer);
        if (pair.getRight()) {
            throw new UnsupportedOperationException();
        }
        
        for (int i = 0, n=pair.getLeft(); i<n; i++) {
            int nameHash = buffer.getInt();
            Pair<Integer, Boolean> pairInner = BinaryObject.getCount(buffer);
            if (pairInner.getRight()) {
                
            } else {
                
            }
        }



    }
    
    public static Pair<Integer, Boolean> getCount(ByteBuffer buffer) {
        byte readValue = buffer.get();
        int childCount = readValue;
        boolean isOffset = false;
        
        if (childCount < 0) {
            childCount+=256;
        }

        if (childCount < 0xFE) {
            
        } else {
            if  (childCount == 0xFE) {
                isOffset = true;
            }
            childCount = buffer.getInt();
        }
        
        return new ImmutablePair<Integer, Boolean>(childCount, isOffset);
    }

}
