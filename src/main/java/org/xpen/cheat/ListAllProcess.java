package org.xpen.cheat;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class ListAllProcess {
    
    public static final int PROCESS_QUERY_INFORMATION = 0x0400;
    public static final int PROCESS_VM_READ = 0x0010;
    public static final int PROCESS_VM_WRITE = 0x0020;
    public static final int PROCESS_VM_OPERATION = 0x0008;
    
    public static void main(String[] args) {
        int[] processlist = new int[1024];
        int[] dummylist = new int[1024];
        Psapi.INSTANCE.EnumProcesses(processlist, 1024, dummylist);

        for (int pid : processlist) {
            System.out.println(pid);
            HANDLE ph = Kernel32.INSTANCE.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ
                    | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, false, pid);


            System.err.println(Kernel32.INSTANCE.GetLastError()); // <- 5
            System.err.println(ph); // <- null
            if (ph != null) {
                byte[] filename = new byte[512];
                Psapi.INSTANCE.GetModuleBaseNameW(ph, new Pointer(0), filename, 512);


                System.err.println(Native.toString(filename));
                Kernel32.INSTANCE.CloseHandle(ph);
            }
            
            System.out.println();

        }

    }

}
