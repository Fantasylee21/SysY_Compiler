package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.*;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;
import llvm.Constant;
import llvm.Function;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.midInstr.CallInstr;
import llvm.midInstr.TruncInstr;
import llvm.midInstr.ZextInstr;
import llvm.midInstr.binaryOperatorTy.BinaryOp;
import llvm.midInstr.binaryOperatorTy.BinaryOperatorTyInstr;
import llvm.midInstr.icmp.IcmpInstr;
import llvm.midInstr.icmp.IcmpOp;
import llvm.type.Int32Type;
import llvm.type.Int8Type;
import llvm.type.LLVMEnumType;

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
    public Integer calcValue() {
        if (children.size() == 1) {
            return children.get(0).calcValue();
        } else if (children.size() == 2) {
            int value = children.get(1).calcValue();
            UnaryOp unaryOp = (UnaryOp) children.get(0);
            TokenNode op = (TokenNode) unaryOp.getChildren().get(0);
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
            return null;
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

    @Override
    public Value generateIR() {
        if (children.size() == 1) {
            return children.get(0).generateIR();
        } else if (children.size() == 2) {
            TokenNode op = (TokenNode) children.get(0).getChildren().get(0);
            Value operand1 = children.get(1).generateIR();
            if (operand1.getType().getType() == LLVMEnumType.Int8Type || operand1.getType().getType() == LLVMEnumType.BoolType) {
                operand1 = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), operand1, Int32Type.getInstance());
            }
            Value operand2 = new Constant(0);
            if (op.getToken().getType() == TokenType.PLUS) {
                return operand1;
            } else if (op.getToken().getType() == TokenType.MINU) {
                return new BinaryOperatorTyInstr(LLVMBuilder.getLlvmBuilder().getVarName(), BinaryOp.SUB, operand2, operand1);
            } else {
                return new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.EQ ,operand2, operand1);
            }
        } else {
            TokenNode ident = (TokenNode) children.get(0);
            FuncSymbol funcSymbol = (FuncSymbol) SymbolManager.getInstance().getSymbol(ident.getToken().getValue(), 1);
            Function function = funcSymbol.getLLVMFunction();
            ArrayList<Value> arguments = new ArrayList<>();
            if (children.size() > 3) {
                FuncRParams funcRParams = (FuncRParams) children.get(2);
                for (int i = 0; i < funcRParams.getChildren().size(); i += 2) {
                    Value value = funcRParams.getChildren().get(i).generateIR();
                    if (value.getType().getType() == LLVMEnumType.Int8Type && funcSymbol.getParams().get(i / 2).getType() == ValueType.Int) {
                        value = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), value, Int32Type.getInstance());
                    } else if (value.getType().getType() == LLVMEnumType.Int32Type && funcSymbol.getParams().get(i / 2).getType() == ValueType.Char) {
                        value = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), value, Int8Type.getInstance());
                    }
                    arguments.add(value);
                }

            }
            if (funcSymbol.getType() == ValueType.Void) {
                return new CallInstr(LLVMBuilder.getLlvmBuilder().getVarName(), function, arguments);
            } else {
                return new CallInstr(LLVMBuilder.getLlvmBuilder().getVarName(), function, arguments);
            }
        }
    }
}
