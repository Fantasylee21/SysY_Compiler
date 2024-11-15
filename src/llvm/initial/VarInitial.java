package llvm.initial;

import llvm.type.LLVMType;

public class VarInitial extends Initial {
    private int value;

    public VarInitial(LLVMType type, String name, int value) {
        super(type, name);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type.toString() + " " + value;
    }
}
