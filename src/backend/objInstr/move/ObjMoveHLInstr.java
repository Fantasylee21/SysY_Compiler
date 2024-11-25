package backend.objInstr.move;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;
import backend.register.Register;

public class ObjMoveHLInstr extends ObjInstr {
    private final MoveType moveType;
    private Register rd;

    public ObjMoveHLInstr(MoveType moveType, Register rd) {
        super();
        this.moveType = moveType;
        this.rd = new Register(rd.getRealRegister(), rd.getVirtualReg());
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Register getRd() {
        return rd;
    }

    public void setRd(Register rd) {
        this.rd = rd;
    }

    @Override
    public String toString() {
        return moveType.toString().toLowerCase() + " " + rd.toString();
    }
}
