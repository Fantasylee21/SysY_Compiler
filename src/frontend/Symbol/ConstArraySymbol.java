package frontend.Symbol;

public class ConstArraySymbol extends Symbol{
    private final int size;

    public ConstArraySymbol(String name, ValueType type) {
        super(name, type);
        this.size = 0;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return name + " Const" + type.toString() + "Array";
    }

    @Override
    public boolean isConst() {
        return true;
    }
}
