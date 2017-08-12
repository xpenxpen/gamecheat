package org.xpen.westwood.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class MixFile {
    public static final byte[] MAGIC_PACKAGE = {0x50, 0x41, 0x43, 0x4B, 0x41, 0x47, 0x45, 0x00}; //PACKAGE

    private static final Logger LOG = LoggerFactory.getLogger(MixFile.class);

    protected RandomAccessFile raf;
    protected FileChannel fileChannel;

    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;

    public MixFile() {
    }

    public MixFile(String fileName) throws Exception {
        this.fileName = fileName;

        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".MIX"), "r");
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
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(6);
        fileChannel.read(buffer);
        buffer.flip();

        int fileCount = buffer.getShort();
        buffer.getInt(); //body count

        for (int i = 0; i < fileCount; i++) {
            buffer = ByteBuffer.allocate(12);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(12);
            fileChannel.read(buffer);
            buffer.flip();

            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            fatEntry.offset = fatEntry.offset + 6 + 12 * fileCount;
            fatEntries.add(fatEntry);
        }

        buffer.clear();
    }

    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);

            raf.seek(fatEntry.offset);

            byte[] bytes = new byte[fatEntry.size];
            raf.readFully(bytes);

            File outFile = null;
            String threeDigit = StringUtils.leftPad(String.valueOf(i), 3, '0');
            outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit + ".txt");
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();

            OutputStream os = new FileOutputStream(outFile);

            IOUtils.write(bytes, os);
            os.close();
        }
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String datFileName;
        public String fname;
        public int crc;
        public int offset;
        public int size;

        public void decode(ByteBuffer buffer) {
            crc = buffer.getInt();
            offset = buffer.getInt();
            size = buffer.getInt();
            //LOG.debug(toString());
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
