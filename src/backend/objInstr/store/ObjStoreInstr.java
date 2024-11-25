package backend.objInstr.store;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjStoreInstr extends ObjInstr {
    private final StoreType storeType;
    private Register rt;
    private Register base;
    private Integer offset;

    public ObjStoreInstr(StoreType storeType, Register rt, Register base, Integer offset) {
        super();
        this.storeType = storeType;
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        this.base = new Register(base.getRealRegister(), base.getVirtualReg());
        this.offset = offset;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public ObjStoreInstr(StoreType storeType, Register rt, Register base, Integer offset, boolean afterAdd) {
        super();
        this.storeType = storeType;
        this.rt = new Register(rt.getRealRegister(), rt.getVirtualReg());
        this.base = new Register(base.getRealRegister(), base.getVirtualReg());
        this.offset = offset;
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

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public void setBase(Register base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return storeType.toString().toLowerCase() + " " + rt + ", " + offset + "(" + base + ")";
    }
}
