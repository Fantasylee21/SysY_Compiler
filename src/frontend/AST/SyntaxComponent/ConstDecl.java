package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.ConstArraySymbol;
import frontend.Symbol.ConstVarSymbol;
import frontend.Symbol.Symbol;
import frontend.Symbol.ValueType;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;
import llvm.Constant;
import llvm.GlobalVariable;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.initial.ArrayInitial;
import llvm.initial.VarInitial;
import llvm.midInstr.AllocaInstr;
import llvm.midInstr.GetElementPtrInstr;
import llvm.midInstr.MidInstr;
import llvm.midInstr.StoreInstr;
import llvm.type.*;

import java.util.ArrayList;
// ConstDecl ==> 'const' 'int'|'char' ConstDef { ',' ConstDef } ';'
public class ConstDecl extends Node {
    private int scopeId;

    public ConstDecl(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    //ConstDef ==> Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    @Override
    public void checkErrors() {
        scopeId = SymbolManager.getInstance().getCurrentScopeId();
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
                ConstInitVal constInitVal = (ConstInitVal) constDef.getChildren().get(constDef.getChildren().size() - 1);
                if (constDef.getChildren().size() == 3) {
                    ConstVarSymbol varSymbol = new ConstVarSymbol(ident.getToken().getValue(), valueType);
                    if (valueType == ValueType.Int) {
                        varSymbol.setInitial((VarInitial) constInitVal.getInitial(Int32Type.getInstance(), ident.getToken().getValue()));
                    }  else {
                        varSymbol.setInitial((VarInitial) constInitVal.getInitial(Int8Type.getInstance(), ident.getToken().getValue()));
                    }
                    boolean success = SymbolManager.getInstance().addSymbol(varSymbol);
                    if (!success) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                    }
                } else {
                    ConstArraySymbol arraySymbol = new ConstArraySymbol(ident.getToken().getValue(), valueType);
                    int size = ((ConstExp) constDef.getChildren().get(2)).calcValue();
                    ArrayType arrayType;
                    if (valueType == ValueType.Int) {
                        arrayType = new ArrayType(Int32Type.getInstance(), size);
                    } else {
                        arrayType = new ArrayType(Int8Type.getInstance(), size);
                    }
                    arraySymbol.setSize(size);
                    ArrayInitial arrayInitial = (ArrayInitial) constInitVal.getInitial(arrayType, ident.getToken().getValue());
                    arrayInitial.init(size);
                    arraySymbol.setInitial(arrayInitial);
                    boolean success = SymbolManager.getInstance().addSymbol(arraySymbol);
                    if (!success) {
                        SymbolErrors.getInstance().addError(ident.getStartLine(), "b");
                    }
                }
            }
        }
        super.checkErrors();
    }

    @Override
    public Value generateIR() {
        LLVMType llvmType;
        TokenNode tokenNode = (TokenNode) children.get(1);
        if (tokenNode.getToken().getType() == TokenType.INTTK) {
            llvmType = Int32Type.getInstance();
        } else {
            llvmType = Int8Type.getInstance();
        }
        for (Node node : children) {
            if (node instanceof ConstDef constDef) {
                TokenNode ident = (TokenNode) constDef.getChildren().get(0);
                String name = ident.getToken().getValue();
                Symbol symbol = SymbolManager.getInstance().getSymbol(name, scopeId);
                if (scopeId == 1) {
                    if (symbol instanceof ConstVarSymbol constVarSymbol) {
                        GlobalVariable globalVariable = new GlobalVariable(new PointerType(llvmType), name, constVarSymbol.getInitial());
                        constVarSymbol.setLLVMValue(globalVariable);
                    } else if (symbol instanceof ConstArraySymbol constArraySymbol) {
                        GlobalVariable globalVariable = new GlobalVariable(new PointerType(new ArrayType(llvmType, constArraySymbol.getSize())), name, constArraySymbol.getInitial());
                        constArraySymbol.setLLVMValue(globalVariable);
                    }
                } else {
                    if (symbol instanceof ConstVarSymbol constVarSymbol) {
                        MidInstr instr = new AllocaInstr(LLVMBuilder.getLlvmBuilder().getVarName(), llvmType);
                        constVarSymbol.setLLVMValue(instr);
                        int value = constVarSymbol.getInitial().getValue();
                        Value constant = new Constant(value);
                        if (llvmType.getType() == LLVMEnumType.Int8Type) {
                            constant.setType(Int8Type.getInstance());
                        }
                        instr = new StoreInstr(null, constant, instr);
                    } else if (symbol instanceof ConstArraySymbol constArraySymbol) {
                        MidInstr instr = new AllocaInstr(LLVMBuilder.getLlvmBuilder().getVarName(), new ArrayType(llvmType, constArraySymbol.getSize()));
                        constArraySymbol.setLLVMValue(instr);
                        Value pointer = instr;
                        for (int i = 0; i < constArraySymbol.getSize(); i++) {
                            instr = new GetElementPtrInstr(LLVMBuilder.getLlvmBuilder().getVarName(), pointer, llvmType, new Constant(i));
                            Constant constant = new Constant(constArraySymbol.getInitial().getValues().get(i));
                            if (llvmType.getType() == LLVMEnumType.Int8Type) {
                                constant.setType(Int8Type.getInstance());
                            }
                            new StoreInstr(null, constant, instr);
                        }
                    }
                }
            }
        }
        return null;
    }
}
