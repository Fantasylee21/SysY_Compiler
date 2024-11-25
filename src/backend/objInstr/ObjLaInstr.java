package backend.objInstr;

import backend.MipsBuilder;
import backend.register.Register;

public class ObjLaInstr extends ObjInstr {
    private Register target;
    private final String label;

    public ObjLaInstr(Register target, String label) {
        super();
        this.target = new Register(target.getRealRegister(), target.getVirtualReg());
        this.label = label;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public Register getTarget() {
        return target;
    }

    public void setTarget(Register target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "la " + target.toString() + ", " + label;
    }
}
