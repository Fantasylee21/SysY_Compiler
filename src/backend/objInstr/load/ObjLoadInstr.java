package backend.objInstr.load;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjLoadInstr extends ObjInstr {
    private final LoadType loadType;
    private Register rt;
    private Register base;
    private Integer offset;


    public ObjLoadInstr(LoadType loadType, Register rt, Register base, Integer offset) {
        super();
        this.loadType = loadType;
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        this.base = new Register(base.getRealRegister(), base.getVirtualReg());
        this.offset = offset;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public ObjLoadInstr(LoadType loadType, Register rt, Register base, Integer offset, boolean afterAdd) {
        super();
        this.loadType = loadType;
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        this.base = new Register(base.getRealRegister(), base.getVirtualReg());
        this.offset = offset;
    }

    public LoadType getLoadType() {
        return loadType;
    }

    public Register getRt() {
        return rt;
    }

    public Register getBase() {
        return base;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setRt(Register rt) {
        this.rt = rt;
    }

    public void setBase(Register base) {
        this.base = base;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return loadType.toString().toLowerCase() + " " + rt + ", " + offset + "(" + base + ")";
    }
}
