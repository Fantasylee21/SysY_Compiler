package llvm.instr.icmp;

import llvm.Value;
import llvm.instr.Instr;
import llvm.instr.InstrType;
import llvm.type.BoolType;

public class IcmpInstr extends Instr {
    private IcmpOp op;

    public IcmpInstr(String name, IcmpOp op, Value operand1, Value operand2) {
        super(BoolType.getInstance(), name, InstrType.ICMP);
        this.op = op;
        addOperand(operand1);
        addOperand(operand2);
    }

    @Override
    public String toString() {
        return name + " = icmp " + op.toString().toLowerCase() + " "+ operands.get(0).getType().toString() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }

}
