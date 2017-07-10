package org.xpen.hantang.fd;

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


public class DatoTest {
	
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width;
    int height;
    BufferedImage bi;

    
    @Test
    public void testDato() throws Exception {
    	Color c = new Color(40, 68,137);
    	System.out.println(c.getRGB());
        String paleteFile = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex/FDOTHER/000.lll";
        InputStream isPalete = new FileInputStream(paleteFile);
        String file = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex/DATO/000.lll";
        InputStream isDato = new FileInputStream(file);
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        getPallete(isPalete);
        //test8080(isDato);
        getPixel();
        close();
        
//        JFrame jFrame = new JFrame();
//        jFrame.setSize(800, 600);
//        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jFrame.add(new PanelImageDisplayer());
//        jFrame.setVisible(true);
    }

	private void test8080(InputStream isDato) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(16);
        fileChannel.read(buffer);
        buffer.flip();
       
		int[] offsets = new int[4];
        for (int i = 0; i < 4; i++) {
        	offsets[i] = buffer.getInt();
            System.out.println("offsets[i]="+offsets[i]);
        }
        
        raf.skipBytes(4);
        
	    int total = 0;
	    while (raf.getFilePointer() < offsets[1]) {
	        int aNum = raf.readUnsignedByte();
	        if ((aNum & 0xC0) == 0xC0) {
	            aNum = aNum & 0x3F;
	            total += aNum;
	            raf.readUnsignedByte();
	        } else {
	            total += 1;
	        }
	        System.out.println(",aNum="+aNum+",total="+total);
	    }
    }

    private void getPallete(InputStream isPalete) throws Exception {
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
        	int r =  isPalete.read();
        	int g =  isPalete.read();
        	int b =  isPalete.read();
        	int a =  0xFF;
        	colors[i] = new Color(r,g,b,a).brighter().brighter().brighter().brighter();
        }
        isPalete.close();
	}

	private void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(20);
        fileChannel.read(buffer);
        buffer.flip();
       
		int[] offsets = new int[4];
        for (int i = 0; i < 4; i++) {
        	offsets[i] = buffer.getInt();
            //System.out.println("offsets[i]="+offsets[i]);
        }
        
        width = buffer.getShort();
        height = buffer.getShort();
        
	    int total = 0;
	    int i = 0;
	    int j = 0;
	    bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    while (raf.getFilePointer() < offsets[1]) {
	        int aNum = raf.readUnsignedByte();
	        if ((aNum & 0xC0) == 0xC0) {
	            aNum = aNum & 0x3F;
	            total += aNum;
	            int colorIndex = raf.readUnsignedByte();
	            for (int k = 0; k < aNum; k++) {
	            	bi.setRGB(i, j, colors[colorIndex].getRGB());
	            	i++;
	            	if (i>=width) {
	            		i=0;
	            		j++;
	            	}
	            }
	        } else {
	            total += 1;
            	bi.setRGB(i, j, colors[aNum].getRGB());
            	i++;
            	if (i>=width) {
            		i=0;
            		j++;
            	}
	        }
	        System.out.println(",aNum="+aNum+",total="+total);
	    }
        
        ImageIO.write(bi, "PNG", new File("G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex/DATO/abc.png"));
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