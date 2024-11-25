package backend.objInstr;

import backend.MipsBuilder;
import backend.objInstr.jump.JumpType;
import backend.register.Register;

public class ObjJRInstr extends ObjInstr {
    private Register rs;

    public ObjJRInstr(Register rs) {
        super();
        this.rs = new Register(rs.getRealRegister(), rs.getVirtualReg());
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public Register getRs() {
        return rs;
    }

    public void setRs(Register rs) {
        this.rs = rs;
    }

    @Override
    public String toString() {
        return "jr " + rs.toString();
    }
}
