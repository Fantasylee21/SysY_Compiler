package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.ArraySymbol;
import frontend.Symbol.Symbol;
import frontend.Symbol.ValueType;
import frontend.Symbol.VarSymbol;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;
import llvm.Constant;
import llvm.GlobalVariable;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.initial.ArrayInitial;
import llvm.initial.VarInitial;
import llvm.midInstr.*;
import llvm.type.*;

import java.util.ArrayList;
// VarDecl ==> 'int'|'char' VarDef { ',' VarDef } ';'
public class VarDecl extends Node {
    private int scopeId;

    public VarDecl(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    //VarDef  ==> Ident [ '[' ConstExp ']' ] [ '=' InitVal]
    @Override
    public void checkErrors() {
        scopeId = SymbolManager.getInstance().getCurrentScopeId();
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
                    VarSymbol varSymbol = getVarSymbol(ident, valueType);
                    boolean success = SymbolManager.getInstance().addSymbol(varSymbol);
                    if (!success) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                    }
                } else {
                    TokenNode node1 = (TokenNode) varDef.getChildren().get(1);
                    boolean hasInitVal = varDef.getChildren().get(varDef.getChildren().size() - 1) instanceof InitVal;
                    if (node1.getToken().getType() == TokenType.LBRACK) {
                        ArraySymbol arraySymbol = new ArraySymbol(ident.getToken().getValue(), valueType);
                        int size = ((ConstExp) varDef.getChildren().get(2)).calcValue();
                        arraySymbol.setSize(size);
                        if (SymbolManager.getInstance().isGlobalScope()) {
                            ArrayType arrayType;
                            if (valueType == ValueType.Int) {
                                arrayType = new ArrayType(Int32Type.getInstance(), size);
                            } else {
                                arrayType = new ArrayType(Int8Type.getInstance(), size);
                            }
                            if (hasInitVal) {
                                InitVal initVal = (InitVal) varDef.getChildren().get(varDef.getChildren().size() - 1);
                                ArrayInitial arrayInitial = (ArrayInitial) initVal.getInitial(arrayType, ident.getToken().getValue());
                                arrayInitial.init(size);
                                arraySymbol.setInitial(arrayInitial);
                            } else {
                                ArrayInitial arrayInitial = new ArrayInitial(arrayType, ident.getToken().getValue(), new ArrayList<>());
                                arrayInitial.init(size);
                                arraySymbol.setInitial(arrayInitial);
                            }
                        }

                        boolean success = SymbolManager.getInstance().addSymbol(arraySymbol);
                        if (!success) {
                            SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                        }
                    } else {
                        VarSymbol varSymbol = new VarSymbol(ident.getToken().getValue(), valueType);
                        if (hasInitVal) {
                            InitVal initVal = (InitVal) varDef.getChildren().get(varDef.getChildren().size() - 1);
                            if (SymbolManager.getInstance().isGlobalScope()) {
                                if (valueType == ValueType.Int) {
                                    varSymbol.setInitial((VarInitial) initVal.getInitial(Int32Type.getInstance(), ident.getToken().getValue()));
                                } else {
                                    varSymbol.setInitial((VarInitial) initVal.getInitial(Int8Type.getInstance(), ident.getToken().getValue()));
                                }
                            }
                        }
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

    private VarSymbol getVarSymbol(TokenNode ident, ValueType valueType) {
        VarSymbol varSymbol = new VarSymbol(ident.getToken().getValue(), valueType);
        if (SymbolManager.getInstance().isGlobalScope()) {
            if (valueType == ValueType.Int) {
                varSymbol.setInitial(new VarInitial(Int32Type.getInstance(), ident.getToken().getValue(), 0));
            } else {
                varSymbol.setInitial(new VarInitial(Int8Type.getInstance(), ident.getToken().getValue(), 0));
            }
        }
        return varSymbol;
    }

    @Override
    public Value generateIR() {
        LLVMType llvmType;
        TokenNode tokenNode = (TokenNode) children.get(0);
        if (tokenNode.getToken().getType() == TokenType.INTTK) {
            llvmType = Int32Type.getInstance();
        } else {
            llvmType = Int8Type.getInstance();
        }
        for (Node node : children) {
            if (node instanceof VarDef varDef) {
                TokenNode ident = (TokenNode) varDef.getChildren().get(0);
                String name = ident.getToken().getValue();
                Symbol symbol = SymbolManager.getInstance().getSymbol(name, scopeId);
                if (scopeId == 1) {
                    if (symbol instanceof VarSymbol varSymbol) {
                        GlobalVariable globalVariable = new GlobalVariable(new PointerType(llvmType), name, varSymbol.getInitial());
                        varSymbol.setLLVMValue(globalVariable);
                    } else if (symbol instanceof ArraySymbol arraySymbol) {
                        GlobalVariable globalVariable = new GlobalVariable(new PointerType(new ArrayType(llvmType, arraySymbol.getSize())), name, arraySymbol.getInitial());
                        arraySymbol.setLLVMValue(globalVariable);
                    }
                } else {
                    if (symbol instanceof VarSymbol varSymbol) {
                        MidInstr instr = new AllocaInstr(LLVMBuilder.getLlvmBuilder().getVarName(), llvmType);
                        varSymbol.setLLVMValue(instr);
                        if (node.getChildren().get(node.getChildren().size() - 1) instanceof InitVal initVal) {
                            Value value = initVal.generateIRList().get(0);
                            if (llvmType.getType() == LLVMEnumType.Int8Type) {
                                if (value instanceof Constant) {
                                    value.setType(Int8Type.getInstance());
                                } else if (value.getType().getType() == LLVMEnumType.Int32Type) {
                                    value = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), value, Int8Type.getInstance());
                                }
                            }
                            instr = new StoreInstr(null, value ,instr);
                        }
                    } else if (symbol instanceof ArraySymbol arraySymbol) {
                        MidInstr instr = new AllocaInstr(LLVMBuilder.getLlvmBuilder().getVarName(), new ArrayType(llvmType, arraySymbol.getSize()));
                        arraySymbol.setLLVMValue(instr);
                        if (node.getChildren().get(node.getChildren().size() - 1) instanceof InitVal initVal) {
                            Value pointer = instr;
                            ArrayList<Value> values = initVal.generateIRList();
                            for (int i = 0; i < values.size(); i++)  {
                                instr = new GetElementPtrInstr(LLVMBuilder.getLlvmBuilder().getVarName(), pointer, llvmType, new Constant(i));
                                if (llvmType.getType() == LLVMEnumType.Int8Type) {
                                    if (values.get(i) instanceof Constant) {
                                        values.get(i).setType(Int8Type.getInstance());
                                    } else {
                                        values.set(i, new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), values.get(i), Int8Type.getInstance()));
                                    }
                                }
                                new StoreInstr(null, values.get(i), instr);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
