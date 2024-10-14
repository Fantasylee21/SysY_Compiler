package frontend.Symbol;

public class VarSymbol extends Symbol {

    public VarSymbol(String name, ValueType type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return name + " " +type.toString();
    }

}
