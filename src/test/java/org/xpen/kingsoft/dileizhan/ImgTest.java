package org.xpen.kingsoft.dileizhan;

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
import javax.swing.JPanel;

import org.junit.Test;


public class ImgTest {
	
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width;
    int height;
    BufferedImage bi;

    
    @Test
    public void testImg() throws Exception {
        String file = "F:/game/抗日地雷战/77-1.IMG";
        InputStream isImg = new FileInputStream(file);
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        getPallete();
        getPixel();
        close();
        
//        JFrame jFrame = new JFrame();
//        jFrame.setSize(800, 600);
//        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jFrame.add(new PanelImageDisplayer());
//        jFrame.setVisible(true);
    }

    private void getPallete() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(16);
        fileChannel.read(buffer);
        buffer.flip();
        
        buffer.position(6);
        width = buffer.getShort() + 1;
        height = buffer.getShort() + 1;
        
        fileChannel.position(width * height + 16);
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
        	int r =  raf.readUnsignedByte() * 4;
        	int g =  raf.readUnsignedByte() * 4;
        	int b =  raf.readUnsignedByte() * 4;
        	int a =  0xFF;
        	colors[i] = new Color(r,g,b,a);
        }
	}

	private void getPixel() throws Exception {
        fileChannel.position(16);
        
	    int total = 0;
	    bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    for (int j = 0; j < height; j++) {
	        for (int i = 0; i < width; i++) {
	            int colorIndex = raf.readUnsignedByte();
            	bi.setRGB(i, j, colors[colorIndex].getRGB());
		    }
	    	
	    }
        
        ImageIO.write(bi, "PNG", new File("F:/game/抗日地雷战/abc.png"));
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