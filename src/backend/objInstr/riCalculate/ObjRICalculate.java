package backend.objInstr.riCalculate;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjRICalculate extends ObjInstr {
    private final RICalculateType calculateType;
    private Register rt;
    private Register rs;
    private Integer immediate;

    public ObjRICalculate(RICalculateType calculateType, Register rt, Register rs, Integer immediate) {
        super();
        this.calculateType = calculateType;
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        this.rs = new Register(rs.getRealRegister(), rs.getVirtualReg());
        this.immediate = immediate;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public ObjRICalculate(RICalculateType calculateType, Register rt, Register rs, Integer immediate, boolean afterAdd) {
        super();
        this.calculateType = calculateType;
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        this.rs = new Register(rs.getRealRegister(), rs.getVirtualReg());
        this.immediate = immediate;
    }

    public RICalculateType getCalculateType() {
        return calculateType;
    }

    public Register getRt() {
        return rt;
    }

    public Register getRs() {
        return rs;
    }

    public void setRt(Register rt) {
        this.rt = rt;
    }

    public void setRs(Register rs) {
        this.rs = rs;
    }

    public void setImmediate(Integer immediate) {
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return calculateType.toString().toLowerCase() + " " + rt.toString() + ", " + rs.toString() + ", " + immediate;
    }
}
