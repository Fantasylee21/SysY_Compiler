package frontend.Symbol;

import llvm.Function;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<Symbol> params = new ArrayList<>();
    private Function llvmFunction;

    public FuncSymbol(String name, ValueType type) {
        super(name, type);
        this.params = new ArrayList<>();
    }

    public int getParamsCount() {
        return params.size();
    }

    public ArrayList<Symbol> getParams() {
        return params;
    }

    public Function getLLVMFunction() {
        return llvmFunction;
    }

    public void setLLVMFunction(Function llvmFunction) {
        this.llvmFunction = llvmFunction;
    }

    public void addParam(Symbol param) {
        if (!params.contains(param)) {
            params.add(param);
        }
    }

    @Override
    public String toString() {
        return name + " " + type.toString() + "Func";
    }

}
