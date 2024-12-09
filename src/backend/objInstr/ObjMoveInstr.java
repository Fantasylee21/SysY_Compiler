package backend.objInstr;

import backend.MipsBuilder;
import backend.register.Register;

public class ObjMoveInstr extends ObjInstr {
    private Register dst;
    private Register src;

    public ObjMoveInstr(Register dst, Register src) {
        super();
        this.dst = new Register(dst.getRealRegister(), dst.getVirtualReg());
        this.src = new Register(src.getRealRegister(), src.getVirtualReg());
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public ObjMoveInstr(Register dst, Register src, boolean after) {
        super();
        this.dst = new Register(dst.getRealRegister(), dst.getVirtualReg());
        this.src = new Register(src.getRealRegister(), src.getVirtualReg());
    }

    public Register getDst() {
        return dst;
    }

    public Register getSrc() {
        return src;
    }

    public void setDst(Register dst) {
        this.dst = dst;
    }

    public void setSrc(Register src) {
        this.src = src;
    }

    @Override
    public String toString() {
        if (dst.toString().equals(src.toString())) {
            return "";
        }
        return "move " + dst.toString() + ", " + src.toString();
    }
}
