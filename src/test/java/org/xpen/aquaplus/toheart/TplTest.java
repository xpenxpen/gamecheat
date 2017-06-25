package org.xpen.aquaplus.toheart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

public class TplTest {
	
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width;
    int height;
    BufferedImage bi;

	@Test
    public void testWriteImg() throws Exception {
		//String file = "D:\\psp\\toheart2\\NoLabel\\PSP_GAME\\USRDIR\\myex\\data\\bg\\B001000.tpl";
		String file = "D:\\psp\\toheart2\\NoLabel\\PSP_GAME\\USRDIR\\myexOld\\data\\cal\\calender3.tpl";
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        getPallete();
        getPixel();
        close();
        
        JFrame jFrame = new JFrame();
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(new PanelImageDisplayer());
        jFrame.setVisible(true);

	}

	private void getPallete() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(48);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(48);
        fileChannel.read(buffer);
        buffer.flip();
        
        buffer.position(16);
        height = buffer.getShort();
        width = buffer.getShort();
        
        buffer = ByteBuffer.allocate(256*4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(256*4);
        fileChannel.position(width*height+48);
        fileChannel.read(buffer);
        buffer.flip();
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
        	int r =  buffer.get() & 0xFF;
        	int g =  buffer.get() & 0xFF;
        	int b =  buffer.get() & 0xFF;
        	int a =  buffer.get() & 0xFF;
        	colors[i] = new Color(r,g,b,a);
        }
	}

	private void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(width*height);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(width*height);
        fileChannel.position(48);
        fileChannel.read(buffer);
        buffer.flip();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
//        for (int j = 0; j < height; j++) {
//            for (int i = 0; i < width; i++) {
//            	int colorIndex = buffer.get() & 0xFF;
//            	bi.setRGB(i, j, colors[colorIndex].getRGB());
//            }
//        }
        int bigRound = width / 16 * 8;
        for (int i = 0; i < width*height/16; i++) {
        	int line = 8 * (i % (width / 16)) + i % bigRound / (width / 16) + i / bigRound * bigRound;
        	//System.out.println("i="+i+"line="+line);
        	buffer.position(line * 16);
            for (int j = 0; j < 16; j++) {
            	int colorIndex = buffer.get() & 0xFF;
            	int x = i % (width/16) * 16 + j;
            	int y = i / (width/16);
            	//System.out.println("x="+x+"y="+y);
            	bi.setRGB(x,  y, colors[colorIndex].getRGB());
            }
            //if (i>=480) {
            //	break;
            //}
            	
        }
        
        ImageIO.write(bi, "PNG", new File("D:\\psp\\toheart2\\NoLabel\\PSP_GAME\\USRDIR\\myex\\data\\bg\\abc.png"));
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
