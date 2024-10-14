package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.ConstArraySymbol;
import frontend.Symbol.ConstVarSymbol;
import frontend.Symbol.ValueType;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;

import java.util.ArrayList;
// ConstDecl ==> 'const' 'int'|'char' ConstDef { ',' ConstDef } ';'
public class ConstDecl extends Node {
    public ConstDecl(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    //ConstDef ==> Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    @Override
    public void checkErrors() {
        ValueType valueType;
        TokenNode tokenNode = (TokenNode) children.get(1);
        if (tokenNode.getToken().getType() == TokenType.INTTK) {
            valueType = ValueType.Int;
        } else {
            valueType = ValueType.Char;
        }
        for (Node node : children) {
            if (node instanceof ConstDef constDef) {
                TokenNode ident = (TokenNode) constDef.getChildren().get(0);
                if (constDef.getChildren().size() == 3) {
                    ConstVarSymbol varSymbol = new ConstVarSymbol(ident.getToken().getValue(), valueType);
                    boolean success = SymbolManager.getInstance().addSymbol(varSymbol);
                    if (!success) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                    }
                } else {
                    ConstArraySymbol arraySymbol = new ConstArraySymbol(ident.getToken().getValue(), valueType);
                    boolean success = SymbolManager.getInstance().addSymbol(arraySymbol);
                    if (!success) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                    }
                }
            }
        }
        super.checkErrors();
    }
}
