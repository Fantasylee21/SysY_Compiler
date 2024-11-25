package backend.objInstr.branch;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjBranchInstr extends ObjInstr {
    private final BranchType branchType;
    private String label;
    private Register rs;
    private Register rt;

    public ObjBranchInstr(BranchType branchType, String label, Register rs, Register rt) {
        super();
        this.branchType = branchType;
        this.label = label;
        this.rs = new Register(rs.getRealRegister(), rs.getVirtualReg());
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        MipsBuilder.getMipsBuilder().addTextInstr(this);
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

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return branchType.toString().toLowerCase() + " " + rs.toString() + ", " + rt.toString() + ", " + label;
    }
}
