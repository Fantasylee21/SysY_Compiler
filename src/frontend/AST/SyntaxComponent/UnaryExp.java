package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.*;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;

import java.util.ArrayList;

// UnaryExp ==> PrimaryExp | Ident '(' [FuncRealParams] ')' | UnaryOp UnaryExp
public class UnaryExp extends Node {
    public UnaryExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public ExpValueType getExpValueType() {
        if (children.size() == 1) {
            return children.get(0).getExpValueType();
        } else if (children.size() == 2) {
            return children.get(1).getExpValueType();
        } else {
            return ExpValueType.CONSTANT;
        }
    }

    @Override
    public int calcValue() {
        if (children.size() == 1) {
            return children.get(0).calcValue();
        } else if (children.size() == 2) {
            int value = children.get(1).calcValue();
            TokenNode op = (TokenNode) children.get(0);
            if (op.getToken().getType() == TokenType.PLUS) {
                return value;
            } else if (op.getToken().getType() == TokenType.MINU) {
                return -value;
            } else {
                if (value == 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

    @Override
    public void checkErrors() {
        if (children.size() == 1) {
            super.checkErrors();
        } else if (children.size() == 2) {
            super.checkErrors();
        } else {
            TokenNode ident = (TokenNode) children.get(0);
            Symbol symbol = SymbolManager.getInstance().getSymbol(ident.getToken().getValue());
            if (symbol == null) {
                SymbolErrors.getInstance().addError(ident.getStartLine(), "c");
            } else if (symbol instanceof FuncSymbol funcSymbol) {
                if (children.size() > 3) {
                    FuncRParams funcRParams = (FuncRParams) children.get(2);
                    // FuncRParams ==> Exp { ',' Exp }
                    int paramsCount = funcRParams.getChildren().size() / 2 + 1;
                    if (paramsCount == funcSymbol.getParams().size()) {
                        checkParamsType(funcSymbol, funcRParams, ident.getStartLine());
                    } else {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "d");
                    }
                } else {
                    //实参个数为0
                    if (!funcSymbol.getParams().isEmpty()) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "d");
                    }
                }

            } else {
                SymbolErrors.getInstance().addError(ident.getStartLine(), "c");
            }
            super.checkErrors();
        }
    }

    //TODO: checkParams type
    //传递数组给变量
    //传递变量给数组
    //传递 char 型数组给 int 型数组
    //传递 int 型数组给 char 型数组
    public void checkParamsType(FuncSymbol funcSymbol, FuncRParams funcRParams, int startLine) {
        for (int i = 0; i < funcRParams.getChildren().size(); i += 2) {
            Symbol param = funcSymbol.getParams().get(i / 2);
            Exp exp = (Exp) funcRParams.getChildren().get(i);

            ExpValueType expValueType = exp.getExpValueType();
            if (param instanceof ArraySymbol arraySymbol) {
                if (expValueType == ExpValueType.CHARVAR || expValueType == ExpValueType.INTVAR || expValueType == ExpValueType.CONSTANT) {
                    SymbolErrors.getInstance().addError(startLine, "e");
                    break;
                } else if (expValueType == ExpValueType.CHARARRAY) {
                    if (arraySymbol.getType() == ValueType.Int) {
                        SymbolErrors.getInstance().addError(startLine, "e");
                        break;
                    }
                } else if (expValueType == ExpValueType.INTARRAY) {
                    if (arraySymbol.getType() == ValueType.Char) {
                        SymbolErrors.getInstance().addError(startLine, "e");
                        break;
                    }
                }
            } else {
                if (expValueType == ExpValueType.CHARARRAY || expValueType == ExpValueType.INTARRAY) {
                    SymbolErrors.getInstance().addError(startLine, "e");
                    break;
                }
            }
        }
    }
}
