package org.xpen.odinsoft.fileformat;

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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.xpen.util.ColorUtil;

/**
 * San guo qun ying zhuan 2
 *
 */
public class Sanguo2ShpTest {
    public static final int MAGIC_TLHS = 0x53484C54; //TLHS
	
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width=64;
    int height=80;
    BufferedImage bi;

    public static void main(String[] args) throws Exception {
		new Sanguo2ShpTest();

	}
    
    public Sanguo2ShpTest() throws Exception {
        //String file = "F:/game/Sango2/Sango2/myex/Sango2/Shape/Cursor.SHP";
        String file = "F:/game/Sango2/Sango2/myex/Sango2/Shape/FACE/FACE001.SHP";
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        decodeHeader();
        getPixel();
        close();
        
        JFrame jFrame = new JFrame();
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new PanelImageDisplayer());
        jFrame.setVisible(true);
    }


    private void decodeHeader() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(0x24);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(0x24);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_TLHS) {
           throw new RuntimeException("bad magic");
        }
        
        buffer.position(0x14);
        width = buffer.getInt();
        height = buffer.getInt();
    }

	private void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(height * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(height * 4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int[] eachLineOffset = new int[height + 1];
        for (int i = 0; i < height; i++) {
            eachLineOffset[i] = buffer.getInt();
        }
        eachLineOffset[height] = (int)raf.length();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        //rgb565
        /*
        1:00 00 03 00 04 21 04 21 04 21 FF FF
        2:00 00 04 00 04 21 7D EF 7D EF 04 21 FF FF
        3:00 00 05 00 04 21 7D EF 7D EF 7D EF 04 21 FF FF
        4:00 00 05 00 04 21 38 C6 FB DE 7D EF 04 21 FF FF
        5:01 00 05 00 04 21 38 C6 7D EF 7D EF 04 21 FF FF
        6:01 00 05 00 04 21 75 AD BA D6 7D EF 04 21 FF FF
        7:02 00 05 00 04 21 38 C6 FB DE 7D EF 04 21 FF FF
        8:02 00 05 00 04 21 75 AD FB DE 7D EF 04 21 FF FF
        9:03 00 05 00 04 21 38 C6 FB DE 7D EF 04 21 FF FF
        */
        for (int j = 0; j < height; j++) {
            System.out.println("starting line " + j + ", raf.pos=" + raf.getFilePointer());
            if (raf.getFilePointer() != eachLineOffset[j]) {
                throw new RuntimeException("Unexpected SHP format(eachLineOffset not align)");
            }
            buffer = ByteBuffer.allocate(eachLineOffset[j + 1] - eachLineOffset[j]);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(eachLineOffset[j + 1] - eachLineOffset[j]);
            fileChannel.read(buffer);
            buffer.flip();
            
            int pixelPos = 0;
            while (buffer.remaining() > 2) {
            	int repeatCount = buffer.getShort();
                System.out.println(repeatCount + " number of transparent color");
                for (int i = pixelPos; i < repeatCount; i++) {
                    bi.setRGB(pixelPos, j, 0); //transparent
                    pixelPos++;
                }
                
                repeatCount = buffer.getShort();
                System.out.println(repeatCount + " number of untransparent color");
                for (int i = 0; i < repeatCount; i++) {
                    int rgb555 = buffer.getShort() & 0xFFFF;
                    Color color = ColorUtil.rgb565ToRgb888(rgb555);
                    bi.setRGB(pixelPos, j, color.getRGB());
                    pixelPos++;
                }
            }
            
            //final 2 bytes must be FFFF
            int final2Bytes = buffer.getShort() & 0xFFFF;
            if (final2Bytes != 0xFFFF) {
                throw new RuntimeException("Unexpected SHP format(not FFFF at line end)");
            }
            
            for (int i = pixelPos; i < width; i++) {
                bi.setRGB(pixelPos, j, 0); //transparent
                pixelPos++;
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
}