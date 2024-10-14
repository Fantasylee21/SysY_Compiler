package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.ArraySymbol;
import frontend.Symbol.ValueType;
import frontend.Symbol.VarSymbol;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;

import java.util.ArrayList;
// VarDecl ==> 'int'|'char' VarDef { ',' VarDef } ';'
public class VarDecl extends Node {
    public VarDecl(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    //VarDef  ==> Ident [ '[' ConstExp ']' ] [ '=' InitVal]
    @Override
    public void checkErrors() {
        ValueType valueType;
        TokenNode tokenNode = (TokenNode) children.get(0);
        if (tokenNode.getToken().getType() == TokenType.INTTK) {
            valueType = ValueType.Int;
        } else {
            valueType = ValueType.Char;
        }
        for (Node node : children) {
            if (node instanceof VarDef varDef) {
                TokenNode ident = (TokenNode) varDef.getChildren().get(0);
                if (varDef.getChildren().size() == 1) {
                    VarSymbol varSymbol = new VarSymbol(ident.getToken().getValue(), valueType);
                    boolean success = SymbolManager.getInstance().addSymbol(varSymbol);
                    if (!success) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                    }
                } else {
                    TokenNode node1 = (TokenNode) varDef.getChildren().get(1);
                    if (node1.getToken().getType() == TokenType.LBRACK) {
                        ArraySymbol arraySymbol = new ArraySymbol(ident.getToken().getValue(), valueType);
                        boolean success = SymbolManager.getInstance().addSymbol(arraySymbol);
                        if (!success) {
                            SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                        }
                    } else {
                        VarSymbol varSymbol = new VarSymbol(ident.getToken().getValue(), valueType);
                        boolean success = SymbolManager.getInstance().addSymbol(varSymbol);
                        if (!success) {
                            SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                        }
                    }
                }
            }
        }
        super.checkErrors();
    }
}
