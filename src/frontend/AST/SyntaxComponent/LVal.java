package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.ArraySymbol;
import frontend.Symbol.Symbol;
import frontend.Symbol.ValueType;
import frontend.Symbol.VarSymbol;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;

import java.util.ArrayList;

// LVal ==> Ident ['[' Exp ']']
public class LVal extends Node {
    public LVal(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    public boolean isConst() {
        TokenNode tokenNode = (TokenNode) children.get(0);
        String name = tokenNode.getToken().getValue();
        Symbol temp = SymbolManager.getInstance().getSymbol(name);
        if (temp == null) {
            return false;
        }
        return temp.isConst();
    }

    @Override
    public ExpValueType getExpValueType() {
        TokenNode ident = (TokenNode) children.get(0);
        Symbol symbol = SymbolManager.getInstance().getSymbol(ident.getToken().getValue());
        if (children.size() == 1) {
            if (symbol instanceof VarSymbol) {
                if (symbol.getType() == ValueType.Char) {
                    return ExpValueType.CHARVAR;
                } else {
                    return ExpValueType.INTVAR;
                }
            } else if (symbol instanceof ArraySymbol) {
                if (symbol.getType() == ValueType.Char) {
                    return ExpValueType.CHARARRAY;
                } else {
                    return ExpValueType.INTARRAY;
                }
            } else {
                return ExpValueType.CONSTANT;
            }
        }
        return ExpValueType.CONSTANT;
    }

    @Override
    public void checkErrors() {
        TokenNode ident = (TokenNode) children.get(0);
        Symbol symbol = SymbolManager.getInstance().getSymbol(ident.getToken().getValue());
        if (symbol == null) {
            SymbolErrors.getInstance().addError(ident.getStartLine(), "c");
        }
        super.checkErrors();
    }
}
