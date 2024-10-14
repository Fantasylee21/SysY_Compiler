package frontend.Symbol;

public class ArraySymbol extends Symbol {
    private final int size;

    public ArraySymbol(String name ,ValueType type) {
        super(name, type);
        this.size = 0;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return name + " " + type.toString() + "Array";
    }
}
