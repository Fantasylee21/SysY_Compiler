package frontend.Symbol;

import llvm.Value;
import llvm.initial.ArrayInitial;

public class ArraySymbol extends Symbol {
    private int size;
    private Value llvmValue;
    private ArrayInitial initial;

    public ArraySymbol(String name ,ValueType type) {
        super(name, type);
        this.size = 0;
        this.initial = null;
    }

    public int getSize() {
        return size;
    }

    public Value getLLVMValue() {
        return llvmValue;
    }

    public void setLLVMValue(Value llvmValue) {
        this.llvmValue = llvmValue;
    }

    @Override
    public String toString() {
        if (initial == null) {
            return name + " " + type.toString() + "Array";
        }
        return name + " " + type.toString() + "Array " + initial.toString();
//        return name + " " + type.toString() + "Array";
    }

    public ArrayInitial getInitial() {
        return initial;
    }

    public void setInitial(ArrayInitial initial) {
        this.initial = initial;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
