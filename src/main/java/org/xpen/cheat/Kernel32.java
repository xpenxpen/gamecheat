package org.xpen.cheat;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
		Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32",
				Kernel32.class);

		boolean ReadProcessMemory(WinNT.HANDLE hProcess, int inBaseAddress,
				Pointer outputBuffer, int nSize,
				IntByReference outNumberOfBytesRead);

		public WinNT.HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle,
				int dwProcessId);

		boolean WriteProcessMemory(WinNT.HANDLE hProcess, int lpBaseAddress,
				byte[] lpBuffer, int nSize, IntByReference lpNumberOfBytesRead);

		int GetLastError();

		public void VirtualAllocEx(Pointer ProcessToAllocateRamIn,
				int AddresToStartAt, int DesiredSizeToAllocate,
				int AllocationType, int ProtectType);

		// Needed for some Windows 7 Versions
		boolean EnumProcesses(int[] ProcessIDsOut, int size, int[] BytesReturned);

		int GetProcessImageFileNameW(WinNT.HANDLE Process, char[] outputname,
				int lenght);

}