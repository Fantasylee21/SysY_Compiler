package frontend;

import frontend.Symbol.FuncSymbol;
import frontend.Symbol.Symbol;
import frontend.Symbol.SymbolTable;
import frontend.Symbol.ValueType;

import java.util.Stack;
import java.util.TreeMap;

public class SymbolManager {
    private static final SymbolManager instance = new SymbolManager();
    private final Stack<SymbolTable> symbolTables;
    private int loopDepth;
    private int scopeNum; // 作用域序号
    private FuncSymbol lastFuncSymbol;
    private final TreeMap<Integer, SymbolTable> symbolTableList;

    private SymbolManager() {
        symbolTables = new Stack<>();
        symbolTables.push(new SymbolTable(1, 1));
        loopDepth = 0;
        scopeNum = 1;
        lastFuncSymbol = null;
        symbolTableList = new TreeMap<>();
    }

    public FuncSymbol getLastFuncSymbol() {
        return lastFuncSymbol;
    }

    public Stack<SymbolTable> getSymbolTables() {
        return symbolTables;
    }

    public int getLoopDepth() {
        return loopDepth;
    }

    public int getScopeNum() {
        return scopeNum;
    }

    public static SymbolManager getInstance() {
        return instance;
    }

    public void enterScope() {
        symbolTables.push(new SymbolTable(++scopeNum, symbolTables.peek().getId()));
    }

    public void exitScope() {
        symbolTableList.put(symbolTables.peek().getId(), symbolTables.peek());
        symbolTables.pop();
    }

    public void enterLoop() {
        loopDepth++;
    }

    public void exitLoop() {
        loopDepth--;
    }

    public void updateLastFuncSymbol(FuncSymbol funcSymbol) {
        lastFuncSymbol = funcSymbol;
    }

    public void print() {
        exitScope();
        for (SymbolTable symbolTable : symbolTableList.values()) {
            symbolTable.print();
        }
    }

    public Symbol getSymbol(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i --) {
            SymbolTable symbolTable = symbolTables.get(i);
            Symbol symbol = symbolTable.get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public boolean hasSymbolInCurScope(String name) {
        // 在当前作用域内查找
        SymbolTable symbolTable = symbolTables.peek();
        Symbol symbol = symbolTable.get(name);
        return symbol != null;
    }

    public boolean addSymbol(Symbol symbol) {
        if (hasSymbolInCurScope(symbol.getName())) {
            return false;
        }
        symbolTables.peek().put(symbol);
        return true;
    }

    public boolean lastFuncSymbolIsVoid() {
        if (lastFuncSymbol == null) {
            return false;
        }
        return lastFuncSymbol.getType() == ValueType.Void;
    }
}
