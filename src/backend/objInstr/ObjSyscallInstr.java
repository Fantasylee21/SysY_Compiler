package backend.objInstr;

import backend.MipsBuilder;

public class ObjSyscallInstr extends ObjInstr {
    public ObjSyscallInstr() {
        super();
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
