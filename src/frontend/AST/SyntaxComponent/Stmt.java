package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;

import java.util.ArrayList;
/*
语句 Stmt → LVal '=' Exp ';' // h
| [Exp] ';'
| Block
| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
| 'break' ';' | 'continue' ';' // m
| 'return' [Exp] ';' // f
| LVal '=' 'getint''('')'';' // h
| LVal '=' 'getchar''('')'';' // h
| 'printf''('StringConst {','Exp}')'';' // l
 */
public class Stmt extends Node {
    public Stmt(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    @Override
    public void checkErrors() {
        if (children.get(0).getType() == SyntaxType.LVal && ((TokenNode) children.get(1)).getToken().getType() == TokenType.ASSIGN) {
            LVal lVal = (LVal) children.get(0);
            if (lVal.isConst()) {
                SymbolErrors.getInstance().addError(lVal.getStartLine(), "h");
            }
            super.checkErrors();
        } else if (children.get(0).getType() == SyntaxType.Token) {
            TokenNode tokenNode = (TokenNode) children.get(0);
            if (tokenNode.getToken().getType() == TokenType.BREAKTK
                    || tokenNode.getToken().getType() == TokenType.CONTINUETK) {
                if (SymbolManager.getInstance().getLoopDepth() < 1) {
                    SymbolErrors.getInstance().addError(tokenNode.getStartLine(), "m");
                }
            } else if (tokenNode.getToken().getType() == TokenType.RETURNTK) {
                if (children.size() == 3 && SymbolManager.getInstance().lastFuncSymbolIsVoid()) {
                    SymbolErrors.getInstance().addError(tokenNode.getStartLine(), "f");
                }
                super.checkErrors();
            } else if (tokenNode.getToken().getType() == TokenType.PRINTFTK) {
                int formatCount = getFormatCount();
                int ExpCount = (children.size() - 4) / 2;
                if (formatCount != ExpCount) {
                    SymbolErrors.getInstance().addError(tokenNode.getStartLine(), "l");
                }
                super.checkErrors();
            } else if (tokenNode.getToken().getType() == TokenType.FORTK) {
                for (Node child : children) {
                    if (child.getType() == SyntaxType.Stmt) {
                        SymbolManager.getInstance().enterLoop();
                        child.checkErrors();
                        SymbolManager.getInstance().exitLoop();
                    } else {
                        child.checkErrors();
                    }
                }
            } else {
                super.checkErrors();
            }
        } else if (children.get(0).getType() == SyntaxType.Block) {
            SymbolManager.getInstance().enterScope();
            super.checkErrors();
            SymbolManager.getInstance().exitScope();
        } else {
            super.checkErrors();
        }
    }

    private int getFormatCount() {
        TokenNode stringConst = (TokenNode) children.get(2);
        int formatCount = 0;
        for (int i = 0; i < stringConst.getToken().getValue().length(); i++) {
            if (stringConst.getToken().getValue().charAt(i) == '%') {
                if (i + 1 < stringConst.getToken().getValue().length()
                        && ((stringConst.getToken().getValue().charAt(i + 1) == 'd')
                        || stringConst.getToken().getValue().charAt(i + 1) == 'c')) {
                    formatCount++;
                }
            }
        }
        return formatCount;
    }
}
