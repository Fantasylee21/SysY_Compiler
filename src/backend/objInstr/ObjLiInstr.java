package backend.objInstr;

import backend.MipsBuilder;
import backend.register.Register;

public class ObjLiInstr extends ObjInstr {
    private Register target;
    private final Integer immediate;

    public ObjLiInstr(Register target, Integer immediate) {
        super();
        this.target = new Register(target.getRealRegister(), target.getVirtualReg());
        this.immediate = immediate;
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
        return "li " + target.toString() + ", " + immediate;
    }


}
