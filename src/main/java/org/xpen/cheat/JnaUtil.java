package org.xpen.cheat;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

/**
 * static methods to allow Java to call Windows code. user32.dll code is as
 * specified in the JNA interface User32.java
 */
public class JnaUtil {
   private static final User32 user32 = User32.INSTANCE;
   private static final Kernel32 kernel32 = Kernel32.INSTANCE;
   private static HWND callBackHwnd;

   public static boolean windowExists(final String startOfWindowName) {
      return !user32.EnumWindows(new User32.WNDENUMPROC() {
         @Override
         public boolean callback(HWND hWnd, Pointer data) {
            char[] windowText = new char[512];
            user32.GetWindowText(hWnd, windowText, 512);
            String wText = Native.toString(windowText).trim();

            if (!wText.isEmpty() && wText.startsWith(startOfWindowName)) {
               return false;
            }
            return true;
         }
      }, null);
   }

   public static List<String> getAllWindowNames() {
      final List<String> windowNames = new ArrayList<String>();
      user32.EnumWindows(new User32.WNDENUMPROC() {

         @Override
         public boolean callback(HWND hWnd, Pointer data) {
             char[] windowText = new char[512];
            user32.GetWindowText(hWnd, windowText, 512);
            String wText = Native.toString(windowText).trim();
            if (!wText.isEmpty()) {
               windowNames.add(wText);
            }
            return true;
         }
      }, null);

      return windowNames;
   }

   public static boolean windowExists(HWND hWnd) {
      return user32.IsWindow(hWnd);
   }

   public static HWND getWinHwnd(final String startOfWindowName) {
      callBackHwnd = null;

      user32.EnumWindows(new User32.WNDENUMPROC() {
         @Override
         public boolean callback(HWND hWnd, Pointer data) {
             char[] windowText = new char[512];
            user32.GetWindowText(hWnd, windowText, 512);
            String wText = Native.toString(windowText).trim();

            if (!wText.isEmpty() && wText.startsWith(startOfWindowName)) {
               callBackHwnd = hWnd;
               return false;
            }
            return true;
         }
      }, null);
      return callBackHwnd;
   }

   public static boolean setForegroundWindow(HWND hWnd) {
      return user32.SetForegroundWindow(hWnd);
   }

   public static HWND getForegroundWindow() {
      return user32.GetForegroundWindow();
   }

   public static String getForegroundWindowText() {
      HWND hWnd = getForegroundWindow();
      int nMaxCount = 512;
      char[] lpString = new char[nMaxCount];
      int getWindowTextResult = user32
            .GetWindowText(hWnd, lpString, nMaxCount);
      if (getWindowTextResult == 0) {
         return "";
      }

      return Native.toString(lpString);
   }

   public static boolean isForegroundWindow(HWND hWnd) {
      return user32.GetForegroundWindow().equals(hWnd);
   }

   public static boolean setForegroundWindow(String startOfWindowName) {
       HWND hWnd = getWinHwnd(startOfWindowName);
      return user32.SetForegroundWindow(hWnd);
   }

   public static Rectangle getWindowRect(HWND hWnd) throws JnaUtilException {
      if (hWnd == null) {
         throw new JnaUtilException(
               "Failed to getWindowRect since Pointer hWnd is null");
      }
      Rectangle result = null;
      RECT rect = new RECT();
      boolean rectOK = user32.GetWindowRect(hWnd, rect);
      if (rectOK) {
         int x = rect.left;
         int y = rect.top;
         int width = rect.right - rect.left;
         int height = rect.bottom - rect.top;
         result = new Rectangle(x, y, width, height);
      }

      return result;
   }

   /**
    * set window at x and y position with w and h width. Set on top of z-order
    * 
    * @param hWnd
    * @param x
    * @param y
    * @param w
    * @param h
    * @return boolean -- did it work?
    */
   public static boolean setWindowPos(HWND hWnd, int x, int y, int w, int h) {
      int uFlags = 0;
      return user32.SetWindowPos(hWnd, new HWND(new Pointer(0)), x, y, w, h, uFlags);
   }

   public static boolean moveWindow(HWND hWnd, int x, int y, int nWidth,
         int nHeight) {
      boolean bRepaint = true;
      return user32.MoveWindow(hWnd, x, y, nWidth, nHeight, bRepaint );
   }

   public static Rectangle getWindowRect(String startOfWindowName)
         throws JnaUtilException {
      HWND hWnd = getWinHwnd(startOfWindowName);
      if (hWnd != null) {
         return getWindowRect(hWnd);
      } else {
         throw new JnaUtilException("Failed to getWindowRect for \""
               + startOfWindowName + "\"");
      }
   }

   public static HWND getWindow(HWND hWnd, DWORD uCmd) {
      return user32.GetWindow(hWnd, uCmd);
   }

   public static String getWindowText(HWND hWnd) {
      int nMaxCount = 512;
      char[] lpString = new char[nMaxCount];
      int result = user32.GetWindowText(hWnd, lpString, nMaxCount);
      if (result == 0) {
         return "";
      }
      return Native.toString(lpString);
   }

   public static HWND getOwnerWindow(HWND hWnd) {
      return user32.GetWindow(hWnd, new DWORD(User32.GW_OWNER));
   }

   public static String getOwnerWindow(String childTitle) {
      HWND hWnd = getWinHwnd(childTitle);
      HWND parentHWnd = getOwnerWindow(hWnd);
      if (parentHWnd == null) {
         return "";
      }
      return getWindowText(parentHWnd);

   }

   public static HWND getNextWindow(HWND hWnd) {
      if (hWnd == null) {
         return null;
      }

      return user32.GetWindow(hWnd, new DWORD(User32.GW_HWNDNEXT));
   }

   public static boolean isWindowVisible(HWND hWnd) {
      return user32.IsWindowVisible(hWnd);
   }

//   public static HWND getParent(HWND hWnd) {
//      return user32.GetParent(hWnd);
//   }

   public static HWND getRoot(HWND hWnd) {
      return user32.GetAncestor(hWnd, User32.GA_ROOT);
   }

   public static LONG_PTR getWindowLongPtr(HWND hWndP, int nIndex) {
      HWND hwnd = new HWND(hWndP.getPointer());
      return user32.GetWindowLongPtr(hwnd, nIndex);
   }

   // **** test method to show that this works.
   // **** be sure to have an empty untitled new NotePad app running
   public static void main(String[] args) throws InterruptedException {
      List<String> winNameList = getAllWindowNames();
      for (String winName : winNameList) {
         System.out.println(winName);
      }

      String[] testStrs = { "Untitled-Notepad", "Untitled - Notepad",
            "Untitled  -  Notepad", "Java-Epic", "Java - Epic", "Fubars rule!",
            "The First Night", "New Tab", "Citrix X", "EHR PROD - SVC" };
      for (String testStr : testStrs) {
         HWND hWnd = getWinHwnd(testStr);
         boolean isWindow = windowExists(hWnd);
         System.out.printf("%-22s %5b %16s %b%n", testStr,
               windowExists(testStr), hWnd, isWindow);
      }

      String notePad = "JDK API 1.6";
      HWND hWnd = getWinHwnd(notePad);
      System.out.println("is it foreground window? " + isForegroundWindow(hWnd));
      boolean foo = setForegroundWindow(notePad);
      System.out.println("foregroundwindow: " + foo);
      Thread.sleep(400);
      System.out.println("is it foreground window? " + isForegroundWindow(hWnd));
      Thread.sleep(1000);
      System.out.println("here A");
      try {
         Rectangle rect = getWindowRect(notePad); //!!
         System.out.println("rect: " + rect);
         Robot robot = new Robot();
         System.out.println("here B");

         BufferedImage img = robot.createScreenCapture(rect);
         File outputfile = new File("d:/abc123.png");
         ImageIO.write(img, "png", outputfile);
         
         System.out.println("here C, img is " + img);
         Thread.sleep(500);
         ImageIcon icon = new ImageIcon(img);
         System.out.println("here D. icon is null? " + icon);
         Thread.sleep(500);
         final JLabel label = new JLabel(icon);
         System.out.println("here E. label is null? " + label);
         Thread.sleep(500);
//         SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//               System.out.println("here F");               
//               JOptionPane.showMessageDialog(null, label);
//               System.out.println("here G");
//            }
//         });

      } catch (AWTException e) {
         e.printStackTrace();
      } catch (JnaUtilException e) {
         e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
   }

}