package llvm.midInstr.binaryOperatorTy;

import backend.MipsBuilder;
import backend.objInstr.ObjLaInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.dm.DmType;
import backend.objInstr.dm.ObjDmInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.move.MoveType;
import backend.objInstr.move.ObjMoveHLInstr;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.riCalculate.RICalculateType;
import backend.objInstr.rrCalculate.ObjRRCalculateInstr;
import backend.objInstr.rrCalculate.RRCalculateType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.GlobalVariable;
import llvm.Value;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;
import llvm.type.Int32Type;

public class BinaryOperatorTyInstr extends MidInstr {
    private BinaryOp op;

    public BinaryOperatorTyInstr(String name, BinaryOp op, Value operand1, Value operand2) {
        super(Int32Type.getInstance(), name, MidInstrType.BINARYOPRATORTY);
        this.op = op;
        addOperand(operand1);
        addOperand(operand2);
    }

    public BinaryOp getOp() {
        return op;
    }

    @Override
    public boolean isDef() {
        return true;
    }

    @Override
    public String toString() {
        return name + " = " + op.toString().toLowerCase() + " "+ type.toString() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }

    @Override
    public void generateMips() {
        Value operand1 = operands.get(0);
        Value operand2 = operands.get(1);
        BinaryOp op = getOp();
        Register ans = new Register(VirtualRegister.getVirtualRegister().getRegister());
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, ans);
        
        Register rs = MipsBuilder.getMipsBuilder().getRegister(operand1);
        if (rs == null && operand1 instanceof GlobalVariable) {
            rs = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(operand1, rs);
            new ObjLaInstr(Register.get$k0(), operand1.getName().substring(1));
            new ObjLoadInstr(LoadType.LW, rs, Register.get$k0(), 0);
        }
        Register rt = MipsBuilder.getMipsBuilder().getRegister(operand2);
        if (rt == null && operand2 instanceof GlobalVariable) {
            rt = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(operand2, rt);
            new ObjLaInstr(Register.get$k0(), operand2.getName().substring(1));
            new ObjLoadInstr(LoadType.LW, rt, Register.get$k0(), 0);
        }

        Integer constant1 = null;
        Integer constant2 = null;
        if (operand1 instanceof Constant) {
            constant1 = Integer.parseInt(operand1.getName());
        }

        if (operand2 instanceof Constant) {
            constant2 = Integer.parseInt(operand2.getName());
        }

        switch (op) {
            case ADD:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 + constant2);
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.ADDIU, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.ADDIU, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.ADDU, ans, rs, rt);
                }
                break;
            case SUB:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 - constant2);
                } else if (constant1 != null) {
                    rs = new Register(VirtualRegister.getVirtualRegister().getRegister());
                    MipsBuilder.getMipsBuilder().addRegisterAllocation(operand1, rs);
                    new ObjLiInstr(rs, constant1);
                    new ObjRRCalculateInstr(RRCalculateType.SUBU, ans, rs, rt);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SUBU, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SUBU, ans, rs, rt);
                }
                break;
            case AND:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 & constant2);
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.ANDI, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.ANDI, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.AND, ans, rs, rt);
                }
                break;
            case OR:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 | constant2);
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.ORI, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.ORI, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.OR, ans, rs, rt);
                }
                break;
            case MUL:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 * constant2);
                } else if (constant1 != null) {
//                    如果constant1可以被2的幂整除
//                    if ((constant1 & (constant1 - 1)) == 0) {
//                        if (constant1 > 0) {
//                            new ObjRICalculate(RICalculateType.SLL, ans, rt, Integer.numberOfTrailingZeros(constant1));
//                        } else {
//                            new ObjRICalculate(RICalculateType.SRL, ans, rt, Integer.numberOfTrailingZeros(constant1));
//                        }
//                    } else {
                        rs = new Register(VirtualRegister.getVirtualRegister().getRegister());
                        new ObjLiInstr(rs, constant1);
                        new ObjDmInstr(DmType.MULT, rs, rt);
                        new ObjMoveHLInstr(MoveType.MFLO, ans);
//                    }
                } else if (constant2 != null) {
//                    if ((constant2 & (constant2 - 1)) == 0) {
//                        if (constant2 > 0) {
//                            new ObjRICalculate(RICalculateType.SLL, ans, rs, Integer.numberOfTrailingZeros(constant2));
//                        } else {
//                            new ObjRICalculate(RICalculateType.SRL, ans, rs, Integer.numberOfTrailingZeros(constant2));
//                        }
//                    } else {
                        rt = new Register(VirtualRegister.getVirtualRegister().getRegister());
                        new ObjLiInstr(rt, constant2);
                        new ObjDmInstr(DmType.MULT, rs, rt);
                        new ObjMoveHLInstr(MoveType.MFLO, ans);
//                    }
                } else {
                    new ObjDmInstr(DmType.MULT, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFLO, ans);
                }
                break;
            case SDIV:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 / constant2);
                } else if (constant1 != null) {
                    rs = new Register(VirtualRegister.getVirtualRegister().getRegister());
                    new ObjLiInstr(rs, constant1);
                    new ObjDmInstr(DmType.DIV, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFLO, ans);
                } else if (constant2 != null) {
                    rt = new Register(VirtualRegister.getVirtualRegister().getRegister());
                    new ObjLiInstr(rt, constant2);
                    new ObjDmInstr(DmType.DIV, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFLO, ans);
                } else {
                    new ObjDmInstr(DmType.DIV, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFLO, ans);
                }
                break;
            case SREM:
                if (constant1 != null && constant2 != null) {
                    new ObjLiInstr(ans, constant1 % constant2);
                } else if (constant1 != null) {
                    rs = new Register(VirtualRegister.getVirtualRegister().getRegister());
                    new ObjLiInstr(rs, constant1);
                    new ObjDmInstr(DmType.DIV, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFHI, ans);
                } else if (constant2 != null) {
                    rt = new Register(VirtualRegister.getVirtualRegister().getRegister());
                    new ObjLiInstr(rt, constant2);
                    new ObjDmInstr(DmType.DIV, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFHI, ans);
                } else {
                    new ObjDmInstr(DmType.DIV, rs, rt);
                    new ObjMoveHLInstr(MoveType.MFHI, ans);
                }
                break;
        }

    }
}
