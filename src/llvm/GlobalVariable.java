package llvm;

import llvm.initial.Initial;
import llvm.type.LLVMType;

public class GlobalVariable extends  GlobalValue {
    private Initial initial;

    public GlobalVariable(LLVMType type, String name, Initial initial) {
        super(type, "@" + name);
        this.initial = initial;
        this.initial.setName(name);
        LLVMBuilder.getLlvmBuilder().addGlobalVariable(this);
    }

    public Initial getInitial() {
        return initial;
    }

    @Override
    public String toString() {
        return name + " = dso_local global " + initial.toString();
    }
}
