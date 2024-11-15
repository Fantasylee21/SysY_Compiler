package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Symbol.*;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import llvm.Constant;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.instr.GetElementPtrInstr;
import llvm.instr.Instr;
import llvm.instr.LoadInstr;
import llvm.instr.ZextInstr;
import llvm.type.Int32Type;
import llvm.type.Int8Type;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;

import java.util.ArrayList;

// LVal ==> Ident ['[' Exp ']']
public class LVal extends Node {
    private int scopeId;

    public LVal(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    public boolean isConst() {
        TokenNode tokenNode = (TokenNode) children.get(0);
        String name = tokenNode.getToken().getValue();
        Symbol temp = SymbolManager.getInstance().getSymbol(name);
        if (temp == null) {
            return false;
        }
        return temp.isConst();
    }

    @Override
    public Integer calcValue() {
        TokenNode ident = (TokenNode) children.get(0);
        String name = ident.getToken().getValue();
        Symbol symbol = SymbolManager.getInstance().getSymbol(name);
        if (symbol == null) {
            return null;
        }
        if (children.size() == 1) {
            if (symbol instanceof VarSymbol) {
                return ((VarSymbol) symbol).getInitial().getValue();
            } else if (symbol instanceof ConstVarSymbol) {
                return ((ConstVarSymbol) symbol).getInitial().getValue();
            }
        }
        return null;
    }

    @Override
    public ExpValueType getExpValueType() {
        TokenNode ident = (TokenNode) children.get(0);
        Symbol symbol = SymbolManager.getInstance().getSymbol(ident.getToken().getValue());
        if (children.size() == 1) {
            if (symbol instanceof VarSymbol) {
                if (symbol.getType() == ValueType.Char) {
                    return ExpValueType.CHARVAR;
                } else {
                    return ExpValueType.INTVAR;
                }
            } else if (symbol instanceof ArraySymbol) {
                if (symbol.getType() == ValueType.Char) {
                    return ExpValueType.CHARARRAY;
                } else {
                    return ExpValueType.INTARRAY;
                }
            } else {
                return ExpValueType.CONSTANT;
            }
        }
        return ExpValueType.CONSTANT;
    }

    @Override
    public void checkErrors() {
        TokenNode ident = (TokenNode) children.get(0);
        Symbol symbol = SymbolManager.getInstance().getSymbol(ident.getToken().getValue());
        scopeId = SymbolManager.getInstance().getSymbolScopeId(ident.getToken().getValue());
        if (symbol == null) {
            SymbolErrors.getInstance().addError(ident.getStartLine(), "c");
        }
        super.checkErrors();
    }

    public Value generateIRForLVal(boolean isLeftAssign) {
        TokenNode ident = (TokenNode) children.get(0);
        Symbol symbol = SymbolManager.getInstance().getSymbol(ident.getToken().getValue(), scopeId);
        LLVMType type = null;
        if (symbol.getType() == ValueType.Char) {
            type = Int8Type.getInstance();
        } else {
            type = Int32Type.getInstance();
        }
        Value expValue = null;
        if (children.size() != 1) {
            expValue = children.get(2).generateIR();
        }
        Instr instr = null;
        if (symbol instanceof VarSymbol varSymbol) {
            if (isLeftAssign) {
                return varSymbol.getLLVMValue();
            }
            instr = new LoadInstr(LLVMBuilder.getLlvmBuilder().getVarName(), varSymbol.getLLVMValue());
            if (instr.getType().getType() == LLVMEnumType.Int8Type) {
                instr =  new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), instr, Int32Type.getInstance());
            }
            return instr;
        } else if (symbol instanceof ArraySymbol arraySymbol) {
            if (children.size() == 1) {
                return new GetElementPtrInstr(LLVMBuilder.getLlvmBuilder().getVarName(), arraySymbol.getLLVMValue(), type, new Constant(0));
            } else {
                instr = new GetElementPtrInstr(LLVMBuilder.getLlvmBuilder().getVarName(), arraySymbol.getLLVMValue(), type, expValue);
                if (isLeftAssign) {
                    return instr;
                }
                instr = new LoadInstr(LLVMBuilder.getLlvmBuilder().getVarName(), instr);
                if (instr.getType().getType() == LLVMEnumType.Int8Type) {
                    instr = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), instr, Int32Type.getInstance());
                }
                return instr;
            }
        } else if (symbol instanceof ConstVarSymbol constVarSymbol) {
            return new Constant(constVarSymbol.getInitial().getValue());
        } else {
            ConstArraySymbol constArraySymbol = (ConstArraySymbol) symbol;
            if (children.size() == 1) {
                return new GetElementPtrInstr(LLVMBuilder.getLlvmBuilder().getVarName(), constArraySymbol.getLLVMValue(), type ,new Constant(0));
            } else {
                instr = new GetElementPtrInstr(LLVMBuilder.getLlvmBuilder().getVarName(), constArraySymbol.getLLVMValue(), type, expValue);
                if (isLeftAssign) {
                    return instr;
                }
                instr = new LoadInstr(LLVMBuilder.getLlvmBuilder().getVarName(), instr);
                if (instr.getType().getType() == LLVMEnumType.Int8Type) {
                    instr = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), instr, Int32Type.getInstance());
                }
                return instr;
            }
        }
    }

}
