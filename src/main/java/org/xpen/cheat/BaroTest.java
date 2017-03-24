package org.xpen.cheat;

import java.util.Optional;

import baro.Baro;
import baro.Memory;
import baro.Process;

public class BaroTest {

    public static void main(String[] args) {
        Optional<Process> process = Baro.processByID(29476);
        Memory mem = process.get().read(0x0018F410, 4);
        System.out.println(mem.getInt());
    }

}
