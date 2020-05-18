package org.xpen.ds.format;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.ByteBufferUtil;

public class Narc {
    
    private static final Logger LOG = LoggerFactory.getLogger(Narc.class);

    public String fileName;
    private byte[] bytes;
    public NarcEntry narcEntry;
    
    public void handle(byte[] b, String fileName) throws Exception {
        this.fileName = fileName;
        this.bytes = b;
        decodeDat();
    }
        
    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        narcEntry = new NarcEntry(this);
        narcEntry.decode(buffer);
    }
    
    public class NarcEntry {
        public DsGenericHeader genericHeader;
        public BtafEntry btaf;
        public BtnfEntry btnf;
        public GmifEntry gmif;
        public int gmifOffset;
        public Narc narc;
        
        public NarcEntry(Narc narc) {
            this.narc = narc;
        }

        public void decode(ByteBuffer buffer) throws Exception {
            genericHeader = new DsGenericHeader();
            genericHeader.decode(buffer);
            btaf = new BtafEntry();
            btaf.decode(buffer);
            btnf = new BtnfEntry(this);
            btnf.decode(buffer);
            gmif = new GmifEntry(this);
            gmif.decode(buffer);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * BTAF
     * 4 magic 'BTAF'
     * 4 sectionSize(including the header)
     * 4 fileCount
     * ----LOOP----
     * 4 startOffset
     * 4 endOffset
     */
    public class BtafEntry {
        public String magic;
        public int sectionSize;
        public int fileCount;
        public int[] startOffset;
        public int[] endOffset;
        
        public void decode(ByteBuffer buffer) throws Exception {
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            fileCount = buffer.getInt();
            startOffset = new int[fileCount];
            endOffset = new int[fileCount];
            for (int i = 0; i < fileCount; i++) {
                startOffset[i] = buffer.getInt();
                endOffset[i] = buffer.getInt();
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * BTNF
     * 4 magic 'BTNF'
     * 4 sectionSize(including the header)
     * 4 offset
     * 2 firstPos
     * 2 dirCount
     * ----LOOP----
     * 1 fileNameLength
     * X file name
     */
    public class BtnfEntry {
        public String magic;
        public int sectionSize;
        public int offset;
        public int firstPos;
        public int dirCount;
        public String[] fileNames;
        public NarcEntry narcEntry;
        
        public BtnfEntry(NarcEntry narcEntry) {
            this.narcEntry = narcEntry;
        }

        public void decode(ByteBuffer buffer) throws Exception {
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            narcEntry.gmifOffset = buffer.position() + sectionSize - 8;
            offset = buffer.getInt();
            firstPos = buffer.getShort();
            dirCount = buffer.getShort();
            fileNames = new String[narcEntry.btaf.fileCount];
            for (int i = 0; i < narcEntry.btaf.fileCount; i++) {
                int fileNameLength = buffer.get();
                fileNames[i] = ByteBufferUtil.getFixedLengthString(buffer, fileNameLength);
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * GMIF
     * 4 magic 'GMIF'
     * 4 sectionSize(including the header)
     * DATA
     */
    public class GmifEntry {
        public String magic;
        public int sectionSize;
        public NarcEntry narcEntry;
        
        public GmifEntry(NarcEntry narcEntry) {
            this.narcEntry = narcEntry;
        }
        
        public void decode(ByteBuffer buffer) throws Exception {
            buffer.position(narcEntry.gmifOffset);
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            for (int i = 0; i < narcEntry.btaf.fileCount; i++) {
                int offset = narcEntry.btaf.startOffset[i] + narcEntry.gmifOffset + 8;
                int length = narcEntry.btaf.endOffset[i] - narcEntry.btaf.startOffset[i];
                byte[] bytes = new byte[length];
                buffer.position(offset);
                buffer.get(bytes);
                FileTypeHandler fileTypeHandler = new SimpleCopyHandler("unknown", false);
                fileTypeHandler.handle(bytes, narcEntry.narc.fileName, narcEntry.btnf.fileNames[i], false);
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
