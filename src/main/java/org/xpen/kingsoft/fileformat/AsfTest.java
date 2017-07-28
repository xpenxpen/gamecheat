package org.xpen.kingsoft.fileformat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ASF 1.00
 *
 */
public class AsfTest {
	
    public static final byte[] MAGIC_ASF = {0x41, 0x53, 0x46, 0x20}; //ASF
    public static final byte[] MAGIC_100 = {0x31, 0x2E, 0x30, 0x30}; //1.00
    public static final byte[] MAGIC_101 = {0x31, 0x2E, 0x30, 0x31}; //1.01
    
    private static final Logger LOG = LoggerFactory.getLogger(AsfTest.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private Header header;
    public Color[] colors;
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    
    BufferedImage bi;

    public static void main(String[] args) throws Exception {
		new AsfTest();

	}
    
    public AsfTest() throws Exception {
        String file = "D:/git/opensource/dunia2/jxqy/myex/asf/unknown/unknown/0003.unknown";
        InputStream isDato = new FileInputStream(file);
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        decodeHeader();
        decodePallete();
        decodeFat();
        decodePixel();
        close();
        
        JFrame jFrame = new JFrame();
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new PanelImageDisplayer());
        jFrame.setVisible(true);
    }


    private void decodeHeader() throws Exception {
        byte[] magic = new byte[4];
        raf.readFully(magic);
        if (!Arrays.equals(magic, MAGIC_ASF)) {
            throw new RuntimeException("bad magic");
        }
        
        byte[] version = new byte[4];
        raf.readFully(version);
        if ((!Arrays.equals(version, MAGIC_100)) && (!Arrays.equals(version, MAGIC_101))) {
            throw new RuntimeException("bad version");
        }
        
        fileChannel.position(0x10);
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(32);
        fileChannel.read(buffer);
        buffer.flip();
        
        header = new Header();
        header.decode(buffer);
        
    }

    private void decodePallete() throws Exception {
        fileChannel.position(0x40);
        colors = new Color[header.colorCount];
        
        for (int i = 0; i < colors.length; i++) {
        	int b =  raf.readUnsignedByte();
        	int g =  raf.readUnsignedByte();
        	int r =  raf.readUnsignedByte();
            int a =  raf.readUnsignedByte();
        	a =  0xFF;
        	colors[i] = new Color(r,g,b,a);
        }
        
	}

    private void decodeFat() throws Exception {
        LOG.debug("{}", raf.getFilePointer());
        for (int i = 0; i < header.frameCount; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(8);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            //fatEntry.datFileName = this.fileName;
            fatEntries.add(fatEntry);
        }
    }

	private void decodePixel() throws Exception {
        for (int k = 0; k < fatEntries.size(); k++) {
            FatEntry fatEntry = fatEntries.get(k);
            raf.seek(fatEntry.offset);
            
            if (k==16) {
                int u=1;
            }
            
            int width = header.width;
            int height = header.height;
            //width = 160;
            //height = 100;
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            //ASF图片的特点就是每个点都能定义透明度
            //1D 00 01 2C 70
            //其中1D 00表示接下来1D个像素，透明度为0
            //01 2C 70表示接下来的01个像素透明度是2C，这个像素的颜色是70
            
            //04 FF 70 33 29 25
            //表示接下来04个像素透明度是FF（即完全不透明），这四个像素的颜色数据是70 33 29 25
            int pixelPos = 0;
            while (pixelPos < width * height) {
                int repeatCount = raf.readUnsignedByte();
                int alphaValue = raf.readUnsignedByte();
                
                if (alphaValue == 0) {
                    for (int i = 0; i < repeatCount; i++) {
                        bi.setRGB(pixelPos % width, pixelPos / width, 0); //transparent
                        pixelPos++;
                    }
                } else {
                    for (int i = 0; i < repeatCount; i++) {
                        int colorIndex = raf.readUnsignedByte();
                        int value = colors[colorIndex].getRGB() | (alphaValue << 24); //semi transparent
                        bi.setRGB(pixelPos % width, pixelPos / width, value);
                        pixelPos++;
                    }
                }
                //LOG.debug("raf.getFilePointer()={}, pixelPos={}", raf.getFilePointer(), pixelPos);
            }
            
            
        }
        
        //ImageIO.write(bi, "PNG", new File("D:\\psp\\toheart2\\NoLabel\\PSP_GAME\\USRDIR\\myex\\data\\bg\\abc.png"));
	}
	
    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
 // Make a subclass of the JPanel
    public class PanelImageDisplayer extends JPanel
    {
       // The paintComponent method of the canvas class is automatically 
       // called when the window is created or resized
       public void paintComponent(Graphics g)
       {
             g.drawImage(bi, 0, 0, null);

       }
    }

    public class Header {
        public int width;
        public int height;
        public int frameCount;
        public int direction;
        public int colorCount;
        public int interval;  //interval(ms/frame)
        public int left;
        public int bottom;

        public void decode(ByteBuffer buffer) {
            width = buffer.getInt();
            height = buffer.getInt();
            frameCount = buffer.getInt();
            direction = buffer.getInt();
            colorCount = buffer.getInt();
            interval = buffer.getInt();
            left = buffer.getInt();
            bottom = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public class FatEntry {
        public String datFileName;
        public String fname;
        public int offset;
        public int size;

        public void decode(ByteBuffer buffer) {
            offset = buffer.getInt();
            size = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
