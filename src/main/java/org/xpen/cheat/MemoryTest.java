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
    public static final int PROCESS_QUERY_INFORMATION = 0x0400;
    public static final int PROCESS_VM_READ = 0x0010;
    public static final int PROCESS_VM_WRITE = 0x0020;
    public static final int PROCESS_VM_OPERATION = 0x0008;

    public static void main(String[] args) {
//        User32.INSTANCE.ShowWindow(hwnd, 9); 
//        User32.INSTANCE.SetForegroundWindow(hwnd);
        String notePad = "Step 8";
//        Pointer hWnd = JnaUtil.getWinHwnd(notePad);
        User32 user32 = User32.INSTANCE;
        Kernel32 kernel32 = Kernel32.INSTANCE;
        
        IntByReference pid = new IntByReference();
        int offset = 0x061D97E0;
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
            WinNT.HANDLE hProc =  kernel32.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ
                    | PROCESS_VM_WRITE | PROCESS_VM_OPERATION,
                    false, pid.getValue());
            boolean result = kernel32.ReadProcessMemory(hProc, offset, memory, buffer, null);
            System.out.println("memory="+memory.getByte(0)+" "+memory.getByte(1)
                    +" "+memory.getByte(2)+" "+memory.getByte(3));
            System.out.println("memory="+memory.getInt(0));
            
            result = kernel32.ReadProcessMemory(hProc, 0x0057C3A0, memory, buffer, null);
            int address1 = memory.getInt(0);
            System.out.println("address1="+Integer.toHexString(address1));
            
            result = kernel32.ReadProcessMemory(hProc, address1+0xC, memory, buffer, null);
            int address2 = memory.getInt(0);
            System.out.println("address2="+Integer.toHexString(address2));
            
            result = kernel32.ReadProcessMemory(hProc, address2+0x14, memory, buffer, null);
            int address3 = memory.getInt(0);
            System.out.println("address3="+Integer.toHexString(address3));
            
            result = kernel32.ReadProcessMemory(hProc, address3, memory, buffer, null);
            int address4 = memory.getInt(0);
            System.out.println("address3="+Integer.toHexString(address4));
            
            result = kernel32.ReadProcessMemory(hProc, address4+0x18, memory, buffer, null);
            int finalValue = memory.getInt(0);
            System.out.println("finalValue="+finalValue);
            
            Memory m = new Memory(4);
            m.setInt(0, 9999);
            
            kernel32.WriteProcessMemory(hProc, address4+0x18, m.getByteArray(0, 4), 4, null);
            System.out.println("write success");
            
            result = kernel32.ReadProcessMemory(hProc, address4+0x18, memory, buffer, null);
            finalValue = memory.getInt(0);
            System.out.println("finalValue="+finalValue);
           
            
        }
    }
}
