package org.xpen.pal.fileformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.LzoCompressor;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.XxTea;

public class CpkFile {
    public static final int MAGIC_RST = 0x1A545352; //'RST'
    public static final int HEADER_LENGTH = 0x80;
    public static final String CIPHER = "Vampire.C.J at Softstar Technology (ShangHai) Co., Ltd";
    //public static final int MAX_FOLDER_LENGTH = 96;
    
    private static final Logger LOG = LoggerFactory.getLogger(CpkFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private HeaderInfo headerInfo;
    
    //3:pal3
    //4:pal4
    private int cpkType = -1;
    
//    private List<Folder> folders = new ArrayList<Folder>();
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String fileName;
    private Map<Integer, String> folderMap = new HashMap<Integer, String>();
    private List<FatEntry> toBeProcessedfolderList = new ArrayList<FatEntry>();
    
    
    public CpkFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".cpk"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * CPK File format
     * 4 'RST'
     * 4 version
     * 4 headerLength
     * 4 flag1
     * 4 folderCount
     * 4 fileCount
     * 4 folderNamesLength
     * 4 fileNamesLength
     * 4 flag2
     * ----folderCount
     * |
     * |  LOOP ---folder_file_count
     * |       |  LOOP file
     * |       ---
     * |
     * ----
     *
     */
    public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.limit(HEADER_LENGTH);
        fileChannel.read(buffer);
        buffer.flip();
        
        headerInfo = new HeaderInfo(this);
        headerInfo.decode(buffer);
        LOG.debug("headerInfo={}", headerInfo);
        
        buffer.clear();
        
        decodeFat();
        decodeDat();
        
    }


    /**
     * 0x0080--0x1080 xxtea
     * 0x1080--       plain
     */
    private void decodeFat() throws Exception {
    	byte[] allFatBytes = null;
    	if (this.cpkType == 4) {
	        //byte[] encryptedBytes = new byte[headerInfo._05dwLenSub * 0x20];
	        //byte[] encryptedBytes = new byte[0x1000];
	    	int byteNum = headerInfo._06dwFileNum * 32;
	    	if (byteNum < 0x1000) {
	    		byteNum = 0x1000;
	    	}
	    	
			byte[] encryptedBytes = new byte[0x1000];
	        raf.seek(0x0080);
	        raf.readFully(encryptedBytes);
	        byte[] decryptedBytes = XxTea.decrypt(encryptedBytes, CIPHER.getBytes(Charset.forName("ISO-8859-1")));
	        allFatBytes = new byte[byteNum];
	        System.arraycopy(decryptedBytes, 0, allFatBytes, 0, 0x1000);
	
	    	if (byteNum > 0x1000) {
	    		raf.readFully(allFatBytes, 0x1000, byteNum - 0x1000);
	    	}
    	} else if (this.cpkType == 3) {
	        allFatBytes = new byte[headerInfo._06dwFileNum * 28];
    		raf.readFully(allFatBytes);
    	}

    	
        debugDumpFat(allFatBytes);
        
        ByteBuffer buffer = ByteBuffer.wrap(allFatBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int i = 0; i < headerInfo._06dwFileNum; i++) {
            FatEntry fatEntry = new FatEntry(this);
            fatEntry.decode(buffer);
            //LOG.debug("i={}, fatEntry={}", i, fatEntry);
            
            //sort folder first. We need build folderMap first
            if (fatEntry.isFolder) {
            	toBeProcessedfolderList.add(fatEntry);
                LOG.debug("i={}, fatEntry={}", i, fatEntry);
            } else {
                fatEntries.add(fatEntry);
            }
        }
        
        folderMap.put(0, "");
        
        while (toBeProcessedfolderList.size() != 0) {
            //LOG.debug("toBeProcessedfolderList.size={}", toBeProcessedfolderList.size());
	        for (int i = toBeProcessedfolderList.size() - 1; i >= 0; i--) {
	        	FatEntry fatEntry = toBeProcessedfolderList.get(i);
	            if (folderMap.containsKey(fatEntry.parent)) {
	                raf.seek(fatEntry._04dwSeek);
	                String folderName = ByteBufferUtil.getNullTerminatedString(raf);
	                String currentFolderName = folderMap.get(fatEntry.parent) + "/" + folderName;
	                folderMap.put(fatEntry.crc, currentFolderName);
	                LOG.debug("putting map, {}, {}", fatEntry.crc, currentFolderName);
	                toBeProcessedfolderList.remove(i);
	            }
	        }
        }
        
        LOG.debug("fatEntries={}", fatEntries);
        
    }

    private void debugDumpFat(byte[] decryptedBytes) throws FileNotFoundException, IOException {
        File outFile = null;
        outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + "fat_dump.bin");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        OutputStream os = new FileOutputStream(outFile);
        
        IOUtils.write(decryptedBytes, os);
        os.close();
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry._04dwSeek);
            
            byte[] bytes = new byte[fatEntry._05dwLenght1];
            raf.readFully(bytes);
            
            byte[] outBytes;
            if (fatEntry.flag == 0x10005 && fatEntry._05dwLenght1 == fatEntry._06dwLenght2) {
            	//plain
            	outBytes = bytes;
            } else if (fatEntry.flag == 0x20001) {
            	//lzo
                outBytes = new byte[fatEntry._06dwLenght2];
                LzoCompressor.decompress(bytes, 0, fatEntry._05dwLenght1, outBytes, 0, fatEntry._06dwLenght2);
            } else {
            	throw new RuntimeException("Unsupportted flag:" + fatEntry.flag);
            }

            
            String fName = ByteBufferUtil.getNullTerminatedString(raf);
            String parentFolder = folderMap.get(fatEntry.parent);

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder, fileName + parentFolder + "/" + fName);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(outBytes, os);
            os.close();
        }
        
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

	public class HeaderInfo {
		private CpkFile cpkFile;
		
	    public int magic; //0x1A545352 "RST"
	    public int _02dwUnk  ; //04
	    public int _03dwIndexSeek  ; //08
	    public int _04dwDataSeek   ; //0C   pal4:100080  pal3:0E0080
	    public int _05dwLenSub  ; //10
	    public int _06dwFileNum ; //14
	    public int _07dwUnk  ; //18
	    public int _08dwUnk  ; //1C
	    public int _09dwUnk  ; //20 == 0x14
	    public int _0AdwUnk  ; //24
	    public int _0BdwUnk  ; //28
	    public int _0CdwFileSize  ; //2C
	    public byte[] _10dwUnk = new byte[80] ; //30~80
	    
	    public HeaderInfo(CpkFile cpkFile) {
			this.cpkFile = cpkFile;
		}

		public void decode(ByteBuffer buffer) {
	        this.magic = buffer.getInt();
	        if (magic != MAGIC_RST) {
	            throw new RuntimeException("bad magic");
	        }
	        
	        this._02dwUnk = buffer.getInt();
            this._03dwIndexSeek = buffer.getInt();
            this._04dwDataSeek = buffer.getInt();
            if (_04dwDataSeek == 0x0E0080) {
            	this.cpkFile.cpkType = 3;
            } else if  (_04dwDataSeek == 0x100080) {
            	this.cpkFile.cpkType = 4;
            } else {
            	throw new RuntimeException("bad DataSeek");
            }
            this._05dwLenSub = buffer.getInt();
            this._06dwFileNum = buffer.getInt();
            this._07dwUnk = buffer.getInt();
            this._08dwUnk = buffer.getInt();
            this._09dwUnk = buffer.getInt();
            this._0AdwUnk = buffer.getInt();
            this._0BdwUnk = buffer.getInt();
    	    this._0CdwFileSize = buffer.getInt();
            buffer.get(this._10dwUnk);

	    }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
	}

    public class FatEntry {
    	private CpkFile cpkFile;
        public int crc;
        public int flag;
        public int parent;
        public int _04dwSeek;
        public int _05dwLenght1;
        public int _06dwLenght2;
        public int _07dwNumber;
        public int _08dwEnd; //only for pal4
        public boolean isFolder;
        
        public FatEntry(CpkFile cpkFile) {
			this.cpkFile = cpkFile;
		}

		public void decode(ByteBuffer buffer) {
            this.crc = buffer.getInt();
            this.flag = buffer.getInt();
            this.parent = buffer.getInt();
            this._04dwSeek = buffer.getInt();
            this._05dwLenght1 = buffer.getInt();
            this._06dwLenght2 = buffer.getInt();
            this._07dwNumber = buffer.getInt();
            
            if (cpkFile.cpkType == 4) {
                this._08dwEnd = buffer.getInt();
            }
            
            if (flag == 0x3 || flag == 0x20011) {
                isFolder = true;
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
