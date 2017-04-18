package org.xpen.forceofnature.pck;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Entry {
    public long start;
    public long length;
    public String fileName;
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
