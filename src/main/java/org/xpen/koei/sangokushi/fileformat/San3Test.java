package org.xpen.koei.sangokushi.fileformat;

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

/**
 * Sangokushi 3
 *
 */
public class San3Test {
	
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width=256;
    int height=400;
    BufferedImage bi;

    public static void main(String[] args) throws Exception {
		new San3Test();

	}
    
    public San3Test() throws Exception {
        String file = "D:/git/opensource/dunia2/dos/games-master/dos/三国志3/SANGO3/KOEI.DAT";
        InputStream isDato = new FileInputStream(file);
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
        
        colors = new Color[8];
        int[] uu = new int[]{0x000000, 0xef3f00, 0x004fef, 0xcf4fef, 0x4faf0f, 0xefbf00, 0x00dfef, 0xefefef};
        
        for (int i = 0; i < colors.length; i++) {
        	int r =  uu[i] & 0xFF;
        	int g =  (uu[i] >>> 8) & 0xFF;
        	int b =  (uu[i] >>> 16) & 0xFF;
        	int a =  0xFF;
        	colors[i] = new Color(r,g,b,a);
        }
        
	}

	private void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(width*height/8*3);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(width*height/8*3);
        fileChannel.read(buffer);
        buffer.flip();
        
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        //三国志里面是用8色的，所以是用每三个字节表示8个像素。
        //比如说三个字节是11000000,11100001,00110101，则用相应位的三个组合在一起就可以得到实际的图像数据是6,6,3,1,0,1,0,3
        
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width / 8; i++) {
            	int b1 = buffer.get() & 0xFF;
                int b2 = buffer.get() & 0xFF;
                int b3 = buffer.get() & 0xFF;
                
                int[] p = new int[8];
                for (int k = 0; k < 8; k++) {
                    p[k] = ((b1 >>> (7 - k)) & 0x1) *4 + ((b2 >>> (7 - k)) & 0x1) * 2 + ((b3 >>> (7 - k)) & 0x1);
                }
                
                for (int k = 0; k < 8; k++) {
            	    bi.setRGB(i * 8 + k, j, colors[p[k]].getRGB());
                }
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