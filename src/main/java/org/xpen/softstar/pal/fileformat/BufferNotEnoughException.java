package org.xpen.softstar.pal.fileformat;

public class BufferNotEnoughException extends RuntimeException {
    
    private static final long serialVersionUID = 6039017803461731751L;
    public int bufferSize;

    public BufferNotEnoughException(int bufferSize) {
        this.bufferSize = bufferSize;
    }

}
