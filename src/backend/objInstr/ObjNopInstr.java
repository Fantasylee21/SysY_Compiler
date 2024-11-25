package backend.objInstr;

import backend.MipsBuilder;

public class ObjNopInstr extends ObjInstr {
    public ObjNopInstr() {
        super();
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    @Override
    public String toString() {
        return "nop";
    }
}
