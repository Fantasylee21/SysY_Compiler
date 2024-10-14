package frontend.Symbol;

public class ConstVarSymbol extends Symbol {

    public ConstVarSymbol(String name, ValueType type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return name + " Const" + type.toString();
    }

    @Override
    public boolean isConst() {
        return true;
    }

}
