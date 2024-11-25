package backend.objInstr.global;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;

public class ObjAsciizInstr extends ObjInstr {
    private final String name;
    private final String value;

    public ObjAsciizInstr(String name, String value) {
        super();
        this.name = name;
        this.value = value;
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    @Override
    public String toString() {
        return name + ": .asciiz \"" + value.replace("\n", "\\n") + "\"";
    }

}
