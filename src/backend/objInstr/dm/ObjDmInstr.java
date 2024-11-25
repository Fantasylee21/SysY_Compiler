package backend.objInstr.dm;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjDmInstr extends ObjInstr {
    private DmType dmType;
    private Register rs;
    private Register rt;

    public ObjDmInstr(DmType dmType, Register rs, Register rt) {
        super();
        this.dmType = dmType;
        this.rs = new Register(rs.getRealRegister(), rs.getVirtualReg());
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public DmType getDmType() {
        return dmType;
    }

    public Register getRs() {
        return rs;
    }

    public Register getRt() {
        return rt;
    }

    public void setRs(Register rs) {
        this.rs = rs;
    }

    public void setRt(Register rt) {
        this.rt = rt;
    }

    @Override
    public String toString() {
        return dmType.toString().toLowerCase() + " " + rs.toString() + ", " + rt.toString();
    }
}
