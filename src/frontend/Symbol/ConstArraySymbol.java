package frontend.Symbol;

import llvm.Value;
import llvm.initial.ArrayInitial;

public class ConstArraySymbol extends Symbol{
    private int size;
    private ArrayInitial initial;
    private Value llvmValue;

    public ConstArraySymbol(String name, ValueType type) {
        super(name, type);
        this.size = 0;
        this.initial = null;
    }

    public int getSize() {
        return size;
    }

    public ArrayInitial getInitial() {
        return initial;
    }

    public Value getLLVMValue() {
        return llvmValue;
    }

    public void setLLVMValue(Value llvmValue) {
        this.llvmValue = llvmValue;
    }

    public void setInitial(ArrayInitial initial) {
        this.initial = initial;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        if (initial == null) {
            return name + " Const" + type.toString() + "Array";
        }
//        return name + " Const" + type.toString() + "Array " + initial.toString();
        return name + " Const" + type.toString() + "Array";
    }

    @Override
    public boolean isConst() {
        return true;
    }
}
