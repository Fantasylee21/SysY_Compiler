package frontend.Symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<Symbol> params = new ArrayList<>();

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
