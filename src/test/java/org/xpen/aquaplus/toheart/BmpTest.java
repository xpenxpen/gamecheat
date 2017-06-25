package org.xpen.aquaplus.toheart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BmpTest {
	
    protected static RandomAccessFile raf;
    protected static FileChannel fileChannel;
    public static Color[] colors;
    static int width;
    static int height;
    static BufferedImage bi;

	public static void main(String[] args) throws Exception {
		String file = "D:\\psp\\toheart2\\NoLabel\\PSP_GAME\\USRDIR\\myex\\data\\bg\\B001000.bmp";
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

	private static void getPallete() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(54);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(54);
        fileChannel.read(buffer);
        buffer.flip();
        
        buffer.position(18);
        width = buffer.getInt();
        height = buffer.getInt();
        
        buffer = ByteBuffer.allocate(256*4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(256*4);
        fileChannel.position(54);
        fileChannel.read(buffer);
        buffer.flip();
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
        	int b =  buffer.get() & 0xFF;
        	int g =  buffer.get() & 0xFF;
        	int r =  buffer.get() & 0xFF;
        	int a =  buffer.get() & 0xFF;
        	colors[i] = new Color(r,g,b,a);
        }
	}

	private static void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(width*height);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(width*height);
        fileChannel.position(54+256*4);
        fileChannel.read(buffer);
        buffer.flip();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int j = height - 1; j >= 0; j--) {
            for (int i = 0; i < width; i++) {
            	int colorIndex = buffer.get() & 0xFF;
            	bi.setRGB(i, j, colors[colorIndex].getRGB());
            }
        }
        
        ImageIO.write(bi, "BMP", new File("D:\\psp\\toheart2\\NoLabel\\PSP_GAME\\USRDIR\\myex\\data\\bg\\abc2.bmp"));
	}
	
    public static void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
 // Make a subclass of the JPanel
    public static class PanelImageDisplayer extends JPanel
    {
       // The paintComponent method of the canvas class is automatically 
       // called when the window is created or resized
       public void paintComponent(Graphics g)
       {
             g.drawImage(bi, 0, 0, null);

       }
    }
}
