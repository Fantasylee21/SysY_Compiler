package frontend.Symbol;

import llvm.Value;
import llvm.initial.VarInitial;

public class VarSymbol extends Symbol {
    private Value llvmValue;
    private VarInitial initial;

    public VarSymbol(String name, ValueType type) {
        super(name, type);
        this.initial = null;
    }

    @Override
    public String toString() {
        if (initial == null) {
            return name + " " +type.toString();
        }
        return name + " " +type.toString() + " " + initial.toString();
//        return name + " " +type.toString();
    }

    public Value getLLVMValue() {
        return llvmValue;
    }

    public void setLLVMValue(Value llvmValue) {
        this.llvmValue = llvmValue;
    }

    public VarInitial getInitial() {
        return initial;
    }

    public void setInitial(VarInitial initial) {
        this.initial = initial;
    }


}
