package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;

import java.util.ArrayList;
// MainFuncDef ==> 'int' 'main' '(' ')' Block
public class MainFuncDef extends Node {
    public MainFuncDef(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    @Override
    public void checkErrors() {
        for (Node child : children) {
            if (child.getType() == SyntaxType.Block) {
                int blockChildrenSize = child.getChildren().size();
                Node rbrace = child.getChildren().get(blockChildrenSize - 1);
                if (child.getChildren().size() <= 2) {
                    SymbolErrors.getInstance().addError(rbrace.getStartLine(), "g");
                } else {
                    if (child.getChildren().get(blockChildrenSize - 2) instanceof Stmt) {
                        Node stmt = child.getChildren().get(blockChildrenSize - 2);
                        if (stmt.getChildren().get(0).getType() != SyntaxType.Token) {
                            SymbolErrors.getInstance().addError(rbrace.getStartLine(), "g");
                        } else {
                            TokenNode tokenNode = (TokenNode) stmt.getChildren().get(0);
                            if (tokenNode.getToken().getType() != TokenType.RETURNTK) {
                                SymbolErrors.getInstance().addError(rbrace.getStartLine(), "g");
                            }
                        }
                    } else {
                        SymbolErrors.getInstance().addError(rbrace.getStartLine(), "g");
                    }
                }

                SymbolManager.getInstance().enterScope();
                child.checkErrors();
                SymbolManager.getInstance().exitScope();
            } else {
                child.checkErrors();
            }
        }


    }
}
