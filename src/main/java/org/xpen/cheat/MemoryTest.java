package org.xpen.cheat;

import java.nio.ByteBuffer;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class MemoryTest {
    public static final int PROCESS_VM_READ = 0x0010;

    public static void main(String[] args) {
//        User32.INSTANCE.ShowWindow(hwnd, 9); 
//        User32.INSTANCE.SetForegroundWindow(hwnd);
        String notePad = "Step 2";
//        Pointer hWnd = JnaUtil.getWinHwnd(notePad);
        User32 user32 = User32.INSTANCE;
        Kernel32 kernel32 = Kernel32.INSTANCE;
        
        IntByReference pid = new IntByReference();
        int offset = 0x192298;
        int buffer = 4;
        Memory memory = new Memory(buffer);
        
        ByteBuffer bufSrc = ByteBuffer.allocateDirect(4);
        bufSrc.put(new byte[]{5,10,15,20});
        ByteBuffer bufDest = ByteBuffer.allocateDirect(4);
        bufDest.put(new byte[]{0,1,2,3});
        Pointer ptrSrc = Native.getDirectBufferPointer(bufSrc);
        Pointer ptrDest = Native.getDirectBufferPointer(bufDest);
        
        
        HWND hwnd = user32.FindWindow(null, notePad);   
        if (hwnd != null) {
            System.out.println("i got the handle");
            user32.GetWindowThreadProcessId(hwnd, pid);
            System.out.println("PID is " + pid.getValue());
            WinNT.HANDLE hProc =  kernel32.OpenProcess(PROCESS_VM_READ, false, pid.getValue());
            boolean result = kernel32.ReadProcessMemory(hProc, offset, memory, buffer, null);
            System.out.println("memory="+memory.getByte(0)+" "+memory.getByte(1)
                    +" "+memory.getByte(2)+" "+memory.getByte(3));
        }
    }
}
