package llvm;

import llvm.type.LLVMType;

import java.util.ArrayList;

public class User extends Value {
    protected ArrayList<Value> operands;

    public User(LLVMType type, String name) {
        super(type, name);
        this.operands = new ArrayList<>();
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperand(Value operand) {
        operands.add(operand);
        if (operand != null) {
            operand.addUse(this);
        }
    }

    public void replaceValue(Value oldValue, Value newValue) {
        for (int i = 0; i < operands.size(); i++) {
            if (operands.get(i) == oldValue) {
                operands.set(i, newValue);
                oldValue.removeUser(this);
                newValue.addUse(this);
            }
        }
    }
}
