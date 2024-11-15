package frontend.Symbol;

import llvm.Value;
import llvm.initial.ArrayInitial;
import llvm.initial.VarInitial;

public class ConstVarSymbol extends Symbol {
    private Value llvmValue;
    private VarInitial initial;

    public ConstVarSymbol(String name, ValueType type) {
        super(name, type);
        this.initial = null;
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

    @Override
    public String toString() {
        if (initial == null) {
            return name + " Const" + type.toString();
        }
        return name + " Const" + type.toString() + " " + initial.toString();
//        return name + " Const" + type.toString();
    }

    @Override
    public boolean isConst() {
        return true;
    }

}
