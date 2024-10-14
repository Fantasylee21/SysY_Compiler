package frontend.Symbol;

public class Symbol {
    protected String name;
    protected ValueType type;

    public Symbol(String name, ValueType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ValueType getType() {
        return type;
    }

    public String toString() {
        return null;
    }

    public boolean isConst() {
        return false;
    }
}
