package org.xpen.kingsoft.fileformat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.LzoCompressor;

public class PakFile {
    public static final byte[] MAGIC_PACKAGE = {0x50, 0x41, 0x43, 0x4B, 0x41, 0x47, 0x45, 0x00}; //PACKAGE
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    
    public PakFile() {
    }
    
    public PakFile(String fileName) throws Exception {
        this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".pak"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * PACKAGE File format
     *
     */
    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        byte[] magic = new byte[8];
        raf.readFully(magic);
        if (!Arrays.equals(magic, MAGIC_PACKAGE)) {
            throw new RuntimeException("bad magic");
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(8);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fileCount = buffer.getInt();
        buffer.getInt(); //Unknown =02
        
        for (int i = 0; i < fileCount; i++) {
            buffer = ByteBuffer.allocate(12);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(12);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            fatEntry.datFileName = this.fileName;
            fatEntries.add(fatEntry);
        }
        
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry1 = fatEntries.get(i);
            FatEntry fatEntry2 = fatEntries.get(i + 1);
            int rawContentSize = fatEntry2.offset - fatEntry1.offset;
            fatEntry1.rawContentSize = rawContentSize;
        }
        
        FatEntry fatEntryLast = fatEntries.get(fatEntries.size() - 1);
        fatEntryLast.rawContentSize = (int)(raf.length() - fatEntryLast.offset);

        
        buffer.clear();
    }

    protected void decodeDat() throws Exception {
        int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            byte[] bytes;
            boolean hasException = false;
            int type = 1;
            
            raf.seek(fatEntry.offset);
            int fileSizeCountedBytes = 0;
            List<Integer> lzoCompressSizes = new ArrayList<Integer>();
            
            //LOOP add them all up, until totalCompressedSize = rawContentSize
            //ex: 02 B0 BC A5 D2 A8 B8 A7 D9 0F 00 04
            // B002
            // A5BC
            // A8D2
            // A7B8
            // FD9
            //+
            //------
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(2);
            fileChannel.read(buffer);
            buffer.flip();
            
            fileSizeCountedBytes += 2;
            int totalCompressedSize = 0;
            int compressedSize = buffer.getShort() & 0xFFFF;
            lzoCompressSizes.add(compressedSize);
            totalCompressedSize += compressedSize;
            
                
            if (compressedSize == 0) {
                //SPECIAL CASE: no compress
            	while (compressedSize == 0) {
                    buffer = ByteBuffer.allocate(2);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    buffer.limit(2);
                    fileChannel.read(buffer);
                    buffer.flip();
                    
                    fileSizeCountedBytes += 2;
                    compressedSize = buffer.getShort() & 0xFFFF;
            	}
                bytes = new byte[fatEntry.rawContentSize - fileSizeCountedBytes];
                raf.readFully(bytes);
                LOG.debug("i={}, fileChannel.position={}, offset={}, compressedSize={}, uncompressedSize={}",
                        i, fileChannel.position(), fatEntry.offset, fatEntry.compressedSize, fatEntry.uncompressedSize);
            } else {
            	
            	//LOOP add them all up, until totalCompressedSize = rawContentSize
                while (totalCompressedSize < fatEntry.rawContentSize - fileSizeCountedBytes) {
                    buffer = ByteBuffer.allocate(2);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    buffer.limit(2);
                    fileChannel.read(buffer);
                    buffer.flip();
                    
                    fileSizeCountedBytes += 2;
                    compressedSize = buffer.getShort() & 0xFFFF;
                    lzoCompressSizes.add(compressedSize);
                    totalCompressedSize += compressedSize;
                }
                
                fatEntry.compressedSize = totalCompressedSize;
                
                LOG.debug("i={}, fileChannel.position={}, offset={}, compressedSize={}, uncompressedSize={}",
                        i, fileChannel.position(), fatEntry.offset, fatEntry.compressedSize, fatEntry.uncompressedSize);
                bytes = new byte[fatEntry.compressedSize];
                raf.readFully(bytes);
                
                if (fatEntry.uncompressedSize == 0) {
                    //size equals
                } else {
                    byte[] ub = new byte[fatEntry.uncompressedSize];
                    try {
                        LzoCompressor.decompress(bytes, 0, fatEntry.compressedSize, ub, 0, fatEntry.uncompressedSize, lzoCompressSizes);
                        bytes = ub;
                    } catch (Exception e) {
                    	errorCount++;
                    	LOG.debug("ERROR, i={}", i);
                    	hasException = true;
                    	bytes = noCompress(fatEntry);
                    }
                }
            }
            String fourDigit = StringUtils.leftPad(String.valueOf(i + 1), 4, '0');
            fatEntry.fname = fourDigit;

        	detectAndHandle(fatEntry, bytes);
        }
        
        System.out.println("errorCount="+errorCount);
    }

	private byte[] noCompress(FatEntry fatEntry) throws IOException {
		byte[] bytes;
		//no compress
		raf.seek(fatEntry.offset);
		bytes = new byte[fatEntry.rawContentSize];
		raf.readFully(bytes);
		return bytes;
	}


    private void detectAndHandle(FatEntry entry, byte[] b) throws Exception {
        String detectedType = PakFileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = PakFileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", false);
        }
        
        boolean isUnknown = true;
        
        fileTypeHandler.handle(b, this.fileName, entry.fname, isUnknown);
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
    public int nameHash(String name) {
        long hashv,eunit;

        hashv = 0;

        for (int i = 0, j = 0; i < name.length(); i++) {
            char unit = name.charAt(i);
            System.out.println((int)unit);
            byte[] bb = new String(new char[]{unit}).getBytes(Charset.forName("GB2312"));
            //handle chinese
            if ((int)unit > 255) {
                //int lower = (int)unit & 0x00FF;
                int lower = bb[0] & 0xFF;
                System.out.println("lower=" + lower);
                eunit = lower & 0xFFFFFFFFL;
                hashv += (eunit*(j+1));
                hashv = hashv % 0x8000000BL;
                System.out.println("hashv1=" + hashv);
                long hash2 = ((~hashv) & 0xFFFFFFFFL) +1;
                System.out.println("hash2=" + hash2);
                hashv = (((hash2 <<4) & (0xFFFFFFFFL)) - hashv)& (0xFFFFFFFFL);
                System.out.println("hashv3=" + hashv);
                j++;
                
                //int higher = ((int)unit >>>8) & 0x00FF;
                int higher = bb[1] & 0xFF;
                System.out.println("higher=" + higher);
                eunit = higher & 0xFFFFFFFFL;
                hashv += (eunit*(j+1));
                hashv = hashv % 0x8000000BL;
                System.out.println("hashv1=" + hashv);
                hash2 = ((~hashv) & 0xFFFFFFFFL) +1;
                System.out.println("hash2=" + hash2);
                hashv = (((hash2 <<4) & (0xFFFFFFFFL)) - hashv)& (0xFFFFFFFFL);
                System.out.println("hashv3=" + hashv);
                j++;
                
                
            } else {
                eunit = unit;
                hashv += (eunit*(j+1));
                hashv = hashv % 0x8000000BL;
                System.out.println("hashv1=" + hashv);
                long hash2 = ((~hashv) & 0xFFFFFFFFL) +1;
                System.out.println("hash2=" + hash2);
                hashv = (((hash2 <<4) & (0xFFFFFFFFL)) - hashv)& (0xFFFFFFFFL);
                System.out.println("hashv3=" + hashv);
                j++;
            }
        }

        hashv ^= 0x12345678L;
        return (int)hashv;

    }
    public int nameHashxx(String name) {
        BigInteger hashv;
        BigInteger eunit;

        hashv = BigInteger.valueOf(0);

        for (int i = 0, j = 0; i < name.length(); i++) {
            char unit = name.charAt(i);
            System.out.println((int)unit);
            byte[] bb = new String(new char[]{unit}).getBytes(Charset.forName("UTF-8"));
            //handle chinese
            if ((int)unit > 255) {
//              int lower = (int)unit & 0x00FF;
//              eunit = lower;
//              hashv += (eunit*(i+1));
//              hashv = hashv % 0x8000000B;
//              hashv = ((~hashv +1) << 4) - hashv;
//              j++;
//              
//              int higher = ((int)unit >>>8) & 0x00FF;
//              eunit = higher;
//              hashv += (eunit*(i+1));
//              hashv = hashv % 0x8000000B;
//              hashv = ((~hashv +1) << 4) - hashv;
//              j++
//              
//              
////                int lower = bb[1];
////                eunit = lower;
////                hashv += (eunit*(i+1));
////                hashv = hashv % 0x8000000B;
////                hashv = ((~hashv +1) << 4) - hashv;
////                i++;
////                
////                int higher = bb[0];
////                eunit = higher;
////                hashv += (eunit*(i+1));
////                hashv = hashv % 0x8000000B;
////                hashv = ((~hashv +1) << 4) - hashv;
                
            } else {
                eunit = BigInteger.valueOf(unit);
                hashv = hashv.add(eunit.multiply(BigInteger.valueOf(j+1)));
                hashv = hashv.mod(BigInteger.valueOf(0x8000000BL));
                System.out.println("hashv1=" + hashv.longValue());
                hashv = (hashv.not().and(BigInteger.valueOf(0xFFFFFFFFL)).add(BigInteger.valueOf(1))).shiftLeft(4).subtract(hashv);
                System.out.println("hashv2=" + hashv);
                j++;
            }
        }

        hashv = hashv.xor(BigInteger.valueOf(0x12345678L));
        return hashv.intValue();

    }

    public class FatEntry {
        public String datFileName;
        public String fname;
        public int crc;
        public int offset;
        public int rawContentSize;
        public int compressedSize;
        public int uncompressedSize;
        public int unknownFlag;

        public void decode(ByteBuffer buffer) {
            crc = buffer.getInt();
            offset = buffer.getInt();
            uncompressedSize = buffer.getInt();
            //LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
