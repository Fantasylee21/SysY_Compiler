package backend.register;

import backend.MipsBuilder;
import llvm.Value;

public class VirtualRegister {
    private static int count = 0;
    private static final VirtualRegister virtualRegister = new VirtualRegister();

    private VirtualRegister() {

    }

    public static VirtualRegister getVirtualRegister() {
        return virtualRegister;
    }

    public String getRegister() {
        return "v$" + count++;
    }
}