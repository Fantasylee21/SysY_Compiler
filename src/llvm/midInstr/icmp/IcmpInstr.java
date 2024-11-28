package llvm.midInstr.icmp;

import backend.MipsBuilder;
import backend.objInstr.ObjLaInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
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
import llvm.type.BoolType;

public class IcmpInstr extends MidInstr {
    private IcmpOp op;

    public IcmpInstr(String name, IcmpOp op, Value operand1, Value operand2) {
        super(BoolType.getInstance(), name, MidInstrType.ICMP);
        this.op = op;
        addOperand(operand1);
        addOperand(operand2);
    }

    public IcmpOp getOp() {
        return op;
    }

    @Override
    public String toString() {
        return name + " = icmp " + op.toString().toLowerCase() + " "+ operands.get(0).getType().toString() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }

    @Override
    public void generateMips() {
        Value operand1 = operands.get(0);
        Value operand2 = operands.get(1);
        IcmpOp op = getOp();
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
            case EQ:
                if (constant1 != null && constant2 != null) {
                    if (constant1.equals(constant2)) {
                        new ObjLiInstr(ans, 1);
                    } else {
                        new ObjLiInstr(ans, 0);
                    }
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.SEQ, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SEQ, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SEQ, ans, rs, rt);
                }
                break;
            case NE:
                if (constant1 != null && constant2 != null) {
                    if (!constant1.equals(constant2)) {
                        new ObjLiInstr(ans, 1);
                    } else {
                        new ObjLiInstr(ans, 0);
                    }
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.SNE, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SNE, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SNE, ans, rs, rt);
                }
                break;
            case SGT:
                if (constant1 != null && constant2 != null) {
                    if (constant1 > constant2) {
                        new ObjLiInstr(ans, 1);
                    } else {
                        new ObjLiInstr(ans, 0);
                    }
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.SLTI, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SGT, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SGT, ans, rs, rt);
                }
                break;
            case SGE:
                if (constant1 != null && constant2 != null) {
                    if (constant1 >= constant2) {
                        new ObjLiInstr(ans, 1);
                    } else {
                        new ObjLiInstr(ans, 0);
                    }
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.SLE, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SGE, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SGE, ans, rs, rt);
                }
                break;
            case SLT:
                if (constant1 != null && constant2 != null) {
                    if (constant1 < constant2) {
                        new ObjLiInstr(ans, 1);
                    } else {
                        new ObjLiInstr(ans, 0);
                    }
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.SGT, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SLTI, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SLT, ans, rs, rt);
                }
                break;
            case SLE:
                if (constant1 != null && constant2 != null) {
                    if (constant1 <= constant2) {
                        new ObjLiInstr(ans, 1);
                    } else {
                        new ObjLiInstr(ans, 0);
                    }
                } else if (constant1 != null) {
                    new ObjRICalculate(RICalculateType.SGE, ans, rt, constant1);
                } else if (constant2 != null) {
                    new ObjRICalculate(RICalculateType.SLE, ans, rs, constant2);
                } else {
                    new ObjRRCalculateInstr(RRCalculateType.SLE, ans, rs, rt);
                }
                break;
        }

    }
}
