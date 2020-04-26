package org.xpen.bethesda.creation.fileformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.bethesda.creation.fileformat.bsa.Folder;
import org.xpen.bethesda.creation.fileformat.bsa.FolderFile;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.DeflateCompressor;

public class BsaFile {
    public static final int MAGIC_BSA = 0x415342; //'BSA'
    public static final int HEADER_LENGTH = 0x24;
    public static final int MAX_FOLDER_LENGTH = 96;
    
    private static final Logger LOG = LoggerFactory.getLogger(BsaFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private int version;
    private int headerLength;
    private int flag1;
    private int flag2;
    private boolean compressed;
    private boolean containsFileNameBlobs;
    private int folderCount;
    private int fileCount;
    private int folderNamesLength;
    private int fileNamesLength;
    private String fileName;
    
    private List<Folder> folders = new ArrayList<Folder>();
    private List<FolderFile> folderFiles = new ArrayList<FolderFile>();
    
    
    public BsaFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".bsa"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * BSA File format
     * 4 'BSA'
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
        ByteBuffer buffer = ByteBuffer.allocate(MAX_FOLDER_LENGTH);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.limit(HEADER_LENGTH);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_BSA) {
            throw new RuntimeException("bad magic");
        }
        
        this.version = buffer.getInt();
        if (version != 104) {
            throw new RuntimeException("unsupported file version");
        }
        
        this.headerLength = buffer.getInt();
        if (headerLength != HEADER_LENGTH) {
            throw new RuntimeException("unsupported header length");
        }
        this.flag1 = buffer.getInt();
        this.compressed = ((flag1 & 0x04) == 0x04);
        this.containsFileNameBlobs = ((flag1 & 0x100) > 0 && version == 0x68);
        this.folderCount = buffer.getInt();
        this.fileCount = buffer.getInt();
        
        this.folderNamesLength= buffer.getInt();
        this.fileNamesLength= buffer.getInt();
        this.flag2 = buffer.getInt();

        LOG.debug("folderCount={}, fileCount={}, folderNamesLength={}, fileNamesLength={},flag1={}, flag2={}, version={}, containsFileNameBlobs={}",
                folderCount, fileCount, folderNamesLength, fileNamesLength,flag1, flag2, version, containsFileNameBlobs);
        
        buffer.clear();
        
        //LOG.debug("fileChannel.position()={}, size()={}", fileChannel.position(), fileChannel.size());
        
        for (int i = 0; i < folderCount; i++) {
            buffer.limit(16);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            
            Folder folder = new Folder();
            folders.add(folder);
            folder.hash = buffer.getLong();
            folder.folderFileCount = buffer.getInt();
            folder.offset = buffer.getInt();
        }
        
        //LOG.debug("folders={}", folders);
        //LOG.debug("fileChannel.position()={}, size()={}", fileChannel.position(), fileChannel.size());
        
        for (int i = 0; i < folderCount; i++) {
            Folder folder = folders.get(i);
            buffer.limit(1);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            
            byte folderPathLength = buffer.get();
            buffer.limit(folderPathLength);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            String folderPath = ByteBufferUtil.getNullTerminatedFixedLengthString(buffer, folderPathLength);
            LOG.debug("folderPath={}", folderPath);
            
            for (int j = 0; j < folder.folderFileCount; j++) {
                FolderFile folderFile = new FolderFile();
                folderFiles.add(folderFile);
                
                buffer.limit(16);
                buffer.rewind();
                fileChannel.read(buffer);
                buffer.flip();
                
                folderFile.hash = buffer.getLong();
                folderFile.fileSize = buffer.getInt();
                folderFile.offset = buffer.getInt();
                folderFile.folderPath = folderPath;
                folderFile.compressed = this.compressed;
            }
            
        }
        
        if (folders.size()!=folderCount) {
            throw new RuntimeException("folders.size()!=folderCount");
        }
        if (folderFiles.size()!=fileCount) {
            throw new RuntimeException("folderFiles.size()!=fileCount");
        }

        //LOG.debug("fileChannel.position()={}, size()={}", fileChannel.position(), fileChannel.size());
        
        buffer = ByteBuffer.allocate(fileNamesLength);
        buffer.limit(fileNamesLength);
        buffer.rewind();
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0; i < fileCount; i++) {
            FolderFile folderFile = folderFiles.get(i);
            folderFile.fileName = ByteBufferUtil.getNullTerminatedString(buffer);
        }
        //LOG.debug("folderFiles={}", folderFiles);
        //LOG.debug("fileChannel.position()={}, size()={}", fileChannel.position(), fileChannel.size());
        
        extract();
        
    }

	private void extract() throws IOException, FileNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
		for (int i = 0; i < fileCount; i++) {
            FolderFile folderFile = folderFiles.get(i);
            
            byte[] b = null;
            raf.seek(folderFile.offset);
            fileChannel.position(folderFile.offset);
            
            byte fileNameLength = 0;
            if (containsFileNameBlobs) {
            	fileNameLength = raf.readByte();
            	raf.seek(folderFile.offset + 1 + fileNameLength);
                fileChannel.position(folderFile.offset + 1 + fileNameLength);
            }
            
            if (folderFile.compressed) {
                //LOG.debug("fileChannel.position={}",fileChannel.position());
                buffer.limit(4);
                buffer.rewind();
                fileChannel.read(buffer);
                buffer.flip();
                
                int uncompressedSize = buffer.getInt();
//                LOG.debug("folderPath={}, fileName={}, folderFile.offset={}, folderFile.fileSize={}, uncompressedSize={}",
//                		folderFile.folderPath, folderFile.fileName, folderFile.offset, folderFile.fileSize, uncompressedSize);
                
                int inbLength = folderFile.fileSize - 4;
                if (containsFileNameBlobs) {
                	inbLength = inbLength - 1 - fileNameLength;
                }
                byte[] inb = new byte[inbLength];
                b = new byte[uncompressedSize];
                //raf.skipBytes(4);
                raf.readFully(inb);
                try {
                    DeflateCompressor.decompress(inb, b);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                b = new byte[folderFile.fileSize];
                raf.readFully(b);
            }
            
            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + folderFile.folderPath.replace('\\', '/') + "/" + folderFile.fileName);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(b, os);
            os.close();

        }
	}

	public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

}
