package frontend.Symbol;

import java.util.ArrayList;

public class SymbolTable {
    private final int id;
    private final int fatherId;
    private final ArrayList<Symbol> table;

    public SymbolTable(int id, int fatherId) {
        this.id = id;
        this.fatherId = fatherId;
        this.table = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void put(Symbol symbol) {
        table.add(symbol);
    }

    public Symbol get(String name) {
        for (Symbol symbol : table) {
            if (symbol.getName().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    public void print() {
        for (Symbol symbol : table) {
            System.out.println(id + " " + symbol.toString());
        }
    }
}
