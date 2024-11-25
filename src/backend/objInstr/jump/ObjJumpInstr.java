package backend.objInstr.jump;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;

public class ObjJumpInstr extends ObjInstr {
    private final JumpType jumpType;
    private String label;

    public ObjJumpInstr(JumpType jumpType, String label) {
        super();
        this.jumpType = jumpType;
        this.label = label;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return jumpType.toString().toLowerCase() + " " + label;
    }
}
