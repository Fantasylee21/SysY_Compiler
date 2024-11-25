package backend.objInstr.global;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;

public class ObjSpaceInstr extends ObjInstr {
    private final String name;
    private final int size;

    public ObjSpaceInstr(String name, int size) {
        super();
        this.name = name;
        this.size = size;
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    @Override
    public String toString() {
        return name + ": .space " + size;
    }

}
