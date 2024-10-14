package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Error.SymbolErrors;

import java.util.ArrayList;
// ForStmt ==> LVal '=' Exp
public class ForStmt extends Node {
    public ForStmt(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    @Override
    public void checkErrors() {
        LVal lVal = (LVal) children.get(0);
        if (lVal.isConst()) {
            SymbolErrors.getInstance().addError(lVal.getStartLine(), "h");
        }
        super.checkErrors();
    }

}
