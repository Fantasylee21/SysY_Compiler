package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.*;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;
import llvm.BasicBlock;
import llvm.Function;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.instr.AllocaInstr;
import llvm.instr.Instr;
import llvm.instr.StoreInstr;
import llvm.type.*;

import java.util.ArrayList;

//FuncDef ==> FuncType Ident '(' [FuncFParams] ')' Block
public class FuncDef extends Node {
    private FuncSymbol funcSymbol;

    public FuncDef(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void checkErrors() {
        funcSymbol = createFuncSymbol();

        // enter scope
        SymbolManager.getInstance().enterScope();
        SymbolManager.getInstance().updateLastFuncSymbol(funcSymbol);

        setFuncParams(funcSymbol);
        for (Node child : children) {
            if (child.getType() == SyntaxType.Block && funcSymbol.getType() != ValueType.Void) {
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
                child.checkErrors();
            } else {
                child.checkErrors();
            }
        }
        SymbolManager.getInstance().exitScope();
        SymbolManager.getInstance().updateLastFuncSymbol(null);

    }

    public FuncSymbol createFuncSymbol() {
        FuncType funcType = (FuncType) children.get(0);
        TokenNode funcTypeNode = (TokenNode) funcType.getChildren().get(0);
        TokenNode IdentNode = (TokenNode) children.get(1);
        ValueType type;
        if (funcTypeNode.getToken().getType() == TokenType.VOIDTK) {
            type = ValueType.Void;
        } else if (funcTypeNode.getToken().getType() == TokenType.INTTK) {
            type = ValueType.Int;
        } else {
            type = ValueType.Char;
        }
        String name = IdentNode.getToken().getValue();
        FuncSymbol funcSymbol = new FuncSymbol(name, type);
        boolean success = SymbolManager.getInstance().addSymbol(funcSymbol);
        if (!success) {
            SymbolErrors.getInstance().addError(IdentNode.getStartLine(), "b");
        }
        return funcSymbol;
    }

    public void setFuncParams(FuncSymbol funcSymbol) {
        if (children.size() == 6) {
            Node funcFParams = children.get(3);
            // FuncFParams ==> FuncFParam { ',' FuncFParam }
            // FuncFParam ==> 'int'|'char' Ident ['[' ']']
            for (Node node : funcFParams.getChildren()) {
                if (node instanceof FuncFParam) {
                    ValueType type = ((TokenNode) node.getChildren().get(0)).getToken().getType() == TokenType.INTTK ? ValueType.Int : ValueType.Char;
                    TokenNode ident = (TokenNode) node.getChildren().get(1);
                    String name = ident.getToken().getValue();
                    if (node.getChildren().size() == 2) {
                        VarSymbol varSymbol = new VarSymbol(name, type);
                        boolean success = SymbolManager.getInstance().addSymbol(varSymbol);
                        if (!success) {
                            SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                        }
                        funcSymbol.addParam(varSymbol);
                    } else {
                        ArraySymbol arraySymbol = new ArraySymbol(name, type);
                        boolean success = SymbolManager.getInstance().addSymbol(arraySymbol);
                        if (!success) {
                            SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                        }
                        funcSymbol.addParam(arraySymbol);
                    }
                }
            }
        }
    }

    @Override
    public Value generateIR() {
        LLVMType returnType = funcSymbol.getType() == ValueType.Int ? llvm.type.Int32Type.getInstance() : funcSymbol.getType() == ValueType.Char ? llvm.type.Int8Type.getInstance() : VoidType.getInstance();
        Function function = new Function(returnType, funcSymbol.getName());
        funcSymbol.setLLVMFunction(function);

//      生成函数参数
        ArrayList<Symbol> params = funcSymbol.getParams();
        Value value = null;
        ArrayList<Value> values = new ArrayList<>();
        for (Symbol param : params) {
            if (param instanceof VarSymbol varSymbol) {
                if (varSymbol.getType() == ValueType.Int) {
                    value = new Value(Int32Type.getInstance(), LLVMBuilder.getLlvmBuilder().getVarName());
                } else {
                    value = new Value(Int8Type.getInstance(), LLVMBuilder.getLlvmBuilder().getVarName());
                }
                values.add(value);
            } else if (param instanceof ArraySymbol arraySymbol) {
                if (arraySymbol.getType() == ValueType.Int) {
                    value = new Value(new PointerType(Int32Type.getInstance()), LLVMBuilder.getLlvmBuilder().getVarName());
                } else {
                    value = new Value(new PointerType(Int8Type.getInstance()), LLVMBuilder.getLlvmBuilder().getVarName());
                }
                values.add(value);
            }
        }
        function.setArguments(values);
        BasicBlock entry = new BasicBlock(LLVMBuilder.getLlvmBuilder().getVarName(function));
        LLVMBuilder.getLlvmBuilder().setCurBlock(entry);
        for (Symbol param : params) {
            value = values.get(params.indexOf(param));
            if (param instanceof VarSymbol varSymbol) {
                Instr instr = new AllocaInstr(LLVMBuilder.getLlvmBuilder().getVarName(), value.getType());
                varSymbol.setLLVMValue(instr);
                instr = new StoreInstr(null, value, instr);
            } else if (param instanceof ArraySymbol arraySymbol) {
                arraySymbol.setLLVMValue(value);
            }
        }
        super.generateIR();
        function.checkReturn();
        LLVMBuilder.getLlvmBuilder().resetRegisterCounter();
        return null;
    }
}
