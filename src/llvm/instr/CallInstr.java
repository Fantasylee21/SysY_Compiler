package llvm.instr;

import llvm.Function;
import llvm.Value;
import llvm.type.Int8Type;
import llvm.type.VoidType;

import java.util.ArrayList;
import java.util.List;

public class CallInstr extends Instr {
    public CallInstr(String name, Function function, ArrayList<Value> arguments) {
        super(function.getReturnType(), name, InstrType.CALL);
        addOperand(function);
        for (Value argument : arguments) {
            addOperand(argument);
        }
    }

    public Function getTargetFunc() {
        return (Function) operands.get(0);
    }

    public List<Value> getArguments() {
        return operands.subList(1, operands.size());
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type instanceof VoidType) {
            sb.append("call void ");
        } else if (type instanceof Int8Type) {
            sb.append(name).append(" = ");
            sb.append("call i8 ");
        } else {
            sb.append(name).append(" = ");
            sb.append("call i32 ");
        }
        sb.append(getTargetFunc().getName()).append("(");
        for (int i = 1; i < operands.size(); i++) {
            sb.append(operands.get(i).getType().toString()).append(" ").append(operands.get(i).getName());
            if (i < operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
