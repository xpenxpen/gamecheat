package org.xpen.cheat;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary {
    Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi", Psapi.class);

    boolean EnumProcesses(int[] ProcessIDsOut, int size, int[] BytesReturned);

    DWORD GetModuleBaseNameW(HANDLE hProcess, Pointer hModule, byte[] lpBaseName, int nSize);
    
    int GetProcessImageFileNameW(HANDLE Process, char[] outputname, int length);

}