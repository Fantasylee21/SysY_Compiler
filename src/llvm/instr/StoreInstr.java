package llvm.instr;

import llvm.LLVMBuilder;
import llvm.Value;
import llvm.type.*;

public class StoreInstr extends Instr {
    public StoreInstr(String name, Value value, Value pointer_to) {
        super(null, name, InstrType.STORE);
        addOperand(value);
        addOperand(pointer_to);
    }

    @Override
    public String toString() {
        return "store " + operands.get(0).getType()+ " " + operands.get(0).getName() + ", " +  operands.get(1).getType() + " " + operands.get(1).getName();
    }
}
