package org.xpen.kingsoft.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.LzoCompressor;
import org.xpen.util.UserSetting;

public class PakFile {
    public static final byte[] MAGIC_PACKAGE = {0x50, 0x41, 0x43, 0x4B, 0x41, 0x47, 0x45, 0x00}; //PACKAGE
    public static final int MAGIC_7x7M = 0x4D37BD37; //'7.7M'
    public static final byte XOR_KEY = (byte)0xF7; //xor key
    public static final int XOR_KEY_INT = 0xF7F7F7F7;
    
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
	    	fatEntries.add(fatEntry);
	    }

        
        buffer.clear();
        

        decodeDat();
        
    }

    protected void decodeDat() throws Exception {
    	int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
        	try {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            //LOOP until byte is 00, add them all up
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
            
            int totalCompressedSize = 0;
            int compressedSize = buffer.getShort();
            if (compressedSize < 0) {
            	compressedSize -= Short.MIN_VALUE * 2;
            }
            totalCompressedSize += compressedSize;
            
            boolean zeroFlag = false;
//            for (;;) {
//                buffer = ByteBuffer.allocate(2);
//    		    buffer.order(ByteOrder.LITTLE_ENDIAN);
//    		    buffer.limit(2);
//                fileChannel.read(buffer);
//                buffer.flip();
//                
//                compressedSize = buffer.getShort();
//                
//                zeroFlag = ((compressedSize & 0xFF) == 0);
//                if (zeroFlag) {
//                	break;
//                }
//                
//                if (compressedSize < 0) {
//                	compressedSize -= Short.MIN_VALUE * 2;
//                }
//                totalCompressedSize += compressedSize;
//            	
//            }
//            
//            //move back 2 bytes
//            raf.seek(raf.getFilePointer() - 2);
            
            while (compressedSize > 0xEE3A) {
                buffer = ByteBuffer.allocate(2);
    		    buffer.order(ByteOrder.LITTLE_ENDIAN);
    		    buffer.limit(2);
                fileChannel.read(buffer);
                buffer.flip();
                
                compressedSize = buffer.getShort();
                if (compressedSize < 0) {
                	compressedSize -= Short.MIN_VALUE * 2;
                }
                totalCompressedSize += compressedSize;
            }
            
            fatEntry.compressedSize = totalCompressedSize;
            
			LOG.debug("i={}, fileChannel.position={}, start={}, compressedSize={}, uncompressedSize={}",
					i, fileChannel.position(), fatEntry.start, fatEntry.compressedSize, fatEntry.uncompressedSize);
            byte[] b = new byte[fatEntry.compressedSize];
            raf.readFully(b);
            
            byte[] ub;
            ub = b;
            if (fatEntry.uncompressedSize == 0) {
            	ub = b;
            } else {
	            ub = new byte[fatEntry.uncompressedSize];
	    		LzoCompressor.decompress(b, 0, fatEntry.compressedSize, ub, 0, fatEntry.uncompressedSize);
            }
            String threeDigit = StringUtils.leftPad(String.valueOf(i), 3, '0');

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit + ".txt");
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(ub, os);
            os.close();
        	} catch (Exception e) {
        		LOG.error("ERROR i={}", i);
        		errorCount++;
        	}
        }
        
        System.out.println("errorCount="+errorCount);
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
//            	int lower = (int)unit & 0x00FF;
//	            eunit = lower;
//	            hashv += (eunit*(i+1));
//	            hashv = hashv % 0x8000000B;
//	            hashv = ((~hashv +1) << 4) - hashv;
//	            j++;
//	            
//            	int higher = ((int)unit >>>8) & 0x00FF;
//	            eunit = higher;
//	            hashv += (eunit*(i+1));
//	            hashv = hashv % 0x8000000B;
//	            hashv = ((~hashv +1) << 4) - hashv;
//	            j++
//            	
//            	
////            	int lower = bb[1];
////	            eunit = lower;
////	            hashv += (eunit*(i+1));
////	            hashv = hashv % 0x8000000B;
////	            hashv = ((~hashv +1) << 4) - hashv;
////	            i++;
////	            
////            	int higher = bb[0];
////	            eunit = higher;
////	            hashv += (eunit*(i+1));
////	            hashv = hashv % 0x8000000B;
////	            hashv = ((~hashv +1) << 4) - hashv;
	            
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
        public String fname;
        public int crc;
        public int start;
        public int compressedSize;
        public int uncompressedSize;
        public int unknownFlag;

		public void decode(ByteBuffer buffer) {
			crc = buffer.getInt();
			start = buffer.getInt();
			uncompressedSize = buffer.getInt();
			//LOG.debug(toString());
		}
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
