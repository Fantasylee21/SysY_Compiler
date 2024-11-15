package llvm.instr.binaryOperatorTy;

import llvm.Value;
import llvm.instr.Instr;
import llvm.instr.InstrType;
import llvm.type.Int32Type;
import llvm.type.LLVMType;

public class BinaryOperatorTyInstr extends Instr {
    private BinaryOp op;

    public BinaryOperatorTyInstr(String name, BinaryOp op, Value operand1, Value operand2) {
        super(Int32Type.getInstance(), name, InstrType.BINARYOPRATORTY);
        this.op = op;
        addOperand(operand1);
        addOperand(operand2);
    }

    public BinaryOp getOp() {
        return op;
    }

    @Override
    public String toString() {
        return name + " = " + op.toString().toLowerCase() + " "+ type.toString() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }
}
