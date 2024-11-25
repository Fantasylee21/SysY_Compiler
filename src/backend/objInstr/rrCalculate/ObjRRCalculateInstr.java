package backend.objInstr.rrCalculate;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjRRCalculateInstr extends ObjInstr {
    private final RRCalculateType calculateType;
    private Register rd;
    private Register rs;
    private Register rt;

    public ObjRRCalculateInstr(RRCalculateType calculateType, Register rd, Register rs, Register rt) {
        super();
        this.calculateType = calculateType;
        this.rd = new Register(rd.getRealRegister(), rd.getVirtualReg());
        this.rs = new Register(rs.getRealRegister(), rs.getVirtualReg());
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public Register getRd() {
        return rd;
    }

    public Register getRs() {
        return rs;
    }

    public Register getRt() {
        return rt;
    }

    public void setRd(Register rd) {
        this.rd = rd;
    }

    public void setRs(Register rs) {
        this.rs = rs;
    }

    public void setRt(Register rt) {
        this.rt = rt;
    }

    @Override
    public String toString() {
        return calculateType.toString().toLowerCase() + " " + rd.toString() + ", " + rs.toString() + ", " + rt.toString();
    }
}
