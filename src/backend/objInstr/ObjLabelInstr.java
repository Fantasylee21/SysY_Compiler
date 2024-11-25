package backend.objInstr;

import backend.MipsBuilder;

public class ObjLabelInstr extends ObjInstr {
    private final String label;

    public ObjLabelInstr(String label) {
        super();
        this.label = label;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    @Override
    public String toString() {
        return "\n" + label + ":";
    }
}
