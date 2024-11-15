package llvm;

import llvm.type.LLVMType;

public class GlobalValue extends Value {
    public GlobalValue(LLVMType type, String name) {
        super(type, name);
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }
}
