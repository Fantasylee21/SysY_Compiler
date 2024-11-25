package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Error.SymbolErrors;
import frontend.SymbolManager;
import frontend.Token.TokenType;
import llvm.*;
import llvm.midInstr.*;
import llvm.midInstr.io.*;
import llvm.type.*;

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

    @Override
    public Value generateIR() {
        if (children.get(0).getType() == SyntaxType.LVal && ((TokenNode) children.get(1)).getToken().getType() == TokenType.ASSIGN) {
            LVal lVal = (LVal) children.get(0);
            Value LValValue = lVal.generateIRForLVal(true);
            LLVMType targetType = ((PointerType) LValValue.getType()).getTargetType();
            if (children.get(2).getType() == SyntaxType.Exp) {
                Value expValue = children.get(2).generateIR();
                if (targetType.getType() == LLVMEnumType.Int8Type && expValue.getType().getType() == LLVMEnumType.Int32Type) {
                    expValue = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), expValue, Int8Type.getInstance());
                } else if (targetType.getType() == LLVMEnumType.Int32Type && expValue.getType().getType() == LLVMEnumType.Int8Type) {
                    expValue = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), expValue, Int32Type.getInstance());
                }
                return new StoreInstr(null, expValue, LValValue);
            } else {
                TokenNode tokenNode = (TokenNode) children.get(2);
                if (tokenNode.getToken().getType() == TokenType.GETINTTK) {
                    Value getIntInstr = new GetIntInstr(LLVMBuilder.getLlvmBuilder().getVarName());
                    if (targetType.getType() == LLVMEnumType.Int8Type) {
                        getIntInstr = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), getIntInstr, targetType);
                    }
                    return new StoreInstr(null, getIntInstr, LValValue);
                } else if (tokenNode.getToken().getType() == TokenType.GETCHARTK) {
                    Value getCharInstr = new GetCharInstr(LLVMBuilder.getLlvmBuilder().getVarName());
                    if (targetType.getType() == LLVMEnumType.Int8Type) {
                        getCharInstr = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), getCharInstr, targetType);
                    }
                    return new StoreInstr(null, getCharInstr, LValValue);
                }
            }
        } else if (children.get(0).getType() == SyntaxType.Token) {
            TokenNode tokenNode = (TokenNode) children.get(0);
            if (tokenNode.getToken().getType() == TokenType.BREAKTK) {
                return new JumpInstr(null, LLVMBuilder.getLlvmBuilder().getCurLoop().getExit());
            } else if (tokenNode.getToken().getType() == TokenType.CONTINUETK) {
                return new JumpInstr(null, LLVMBuilder.getLlvmBuilder().getCurLoop().getUpdate());
            } else if (tokenNode.getToken().getType() == TokenType.RETURNTK) {
                Value retValue = null;
                LLVMType returnType = LLVMBuilder.getLlvmBuilder().getCurFunction().getReturnType();
                if (children.size() == 3) {
                    retValue = children.get(1).generateIR();
                }
                if (returnType.getType() == LLVMEnumType.Int8Type && retValue != null && retValue.getType().getType() == LLVMEnumType.Int32Type) {
                    retValue = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), retValue, Int8Type.getInstance());
                }
                if (returnType.getType() == LLVMEnumType.Int32Type && retValue != null && retValue.getType().getType() == LLVMEnumType.Int8Type) {
                    retValue = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), retValue, Int32Type.getInstance());
                }
                return new RetInstr(null, retValue);
            } else if (tokenNode.getToken().getType() == TokenType.PRINTFTK) {
                ArrayList<Value> expValues = new ArrayList<>();
                for (Node node : getChildren()) {
                    if (node instanceof Exp exp) {
                        expValues.add(exp.generateIR());
                    }
                }
                String formatString = ((TokenNode) children.get(2)).getToken().getValue();
                StringBuilder sb = new StringBuilder();
                int expIndex = 0;
                for (int i = 1; i < formatString.length() - 1; i++) {
                    if (formatString.charAt(i) == '%') {
                        if (!sb.isEmpty()) {
                            PrintString printString = new PrintString(LLVMBuilder.getLlvmBuilder().getPrintStringName(), sb.toString());
                            MidInstr instr = new PutStrInstr(null, printString);
                            sb = new StringBuilder();
                        }
                        if (formatString.charAt(i + 1) == 'd') {
                            Value expValue = expValues.get(expIndex++);
                            if (expValue.getType().getType() == LLVMEnumType.Int8Type) {
                                expValue = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), expValue, Int32Type.getInstance());
                            }
                            MidInstr instr = new PutIntInstr(null, expValue);
                            i++;
                        } else if (formatString.charAt(i + 1) == 'c') {
                            Value expValue = expValues.get(expIndex++);
                            if (expValue.getType().getType() == LLVMEnumType.Int8Type) {
                                expValue = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), expValue, Int32Type.getInstance());
                            }
                            MidInstr instr = new PutChInstr(null, expValue);
                            i++;
                        }
                    } else if (formatString.charAt(i) == '\\') {
                        if (formatString.charAt(i + 1) == 'n') {
                            sb.append('\n');
                            i++;
                        } else if (formatString.charAt(i + 1) == 't') {
                            sb.append('\t');
                            i++;
                        } else {
                            sb.append(formatString.charAt(i));
                        }
                    } else {
                        sb.append(formatString.charAt(i));
                    }
                }
                if (!sb.isEmpty()) {
                    PrintString printString = new PrintString(LLVMBuilder.getLlvmBuilder().getPrintStringName(), sb.toString());
                    MidInstr instr = new PutStrInstr(null, printString);
                }
                return null;
            } else if (tokenNode.getToken().getType() == TokenType.FORTK) {
                if (children.get(2).getType() == SyntaxType.ForStmt) {
                    children.get(2).generateIR();
                }

                BasicBlock condBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                BasicBlock bodyBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                BasicBlock updateBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                BasicBlock exitBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());

                LLVMBuilder.getLlvmBuilder().getLoopStack().push(new Loop(condBlock, bodyBlock, updateBlock, exitBlock));

                LLVMBuilder.getLlvmBuilder().removeBasicBlock(bodyBlock.getName());
                LLVMBuilder.getLlvmBuilder().removeBasicBlock(updateBlock.getName());
                LLVMBuilder.getLlvmBuilder().removeBasicBlock(exitBlock.getName());

                new JumpInstr(null, condBlock);

                LLVMBuilder.getLlvmBuilder().setCurBlock(condBlock);

                for (Node child : children) {
                    if (child.getType() == SyntaxType.Cond) {
                        Cond cond = (Cond) child;
                        cond.generateIRForCond(bodyBlock, exitBlock);
                    }
                }

                new JumpInstr(null, bodyBlock);

                LLVMBuilder.getLlvmBuilder().addBasicBlock(bodyBlock);
                LLVMBuilder.getLlvmBuilder().setCurBlock(bodyBlock);
                children.get(children.size() - 1).generateIR();

                new JumpInstr(null, updateBlock);

                LLVMBuilder.getLlvmBuilder().addBasicBlock(updateBlock);
                LLVMBuilder.getLlvmBuilder().setCurBlock(updateBlock);
                if (children.get(children.size() - 3).getType() == SyntaxType.ForStmt) {
                    children.get(children.size() - 3).generateIR();
                }

                new JumpInstr(null, condBlock);

                LLVMBuilder.getLlvmBuilder().addBasicBlock(exitBlock);
                LLVMBuilder.getLlvmBuilder().getLoopStack().pop();
                LLVMBuilder.getLlvmBuilder().setCurBlock(exitBlock);

                return null;
            } else if (tokenNode.getToken().getType() == TokenType.IFTK) {
                BasicBlock thenBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                if (children.size() == 7) {
                    BasicBlock followBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                    BasicBlock elseBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());

                    LLVMBuilder.getLlvmBuilder().removeBasicBlock(thenBlock.getName());
                    LLVMBuilder.getLlvmBuilder().removeBasicBlock(elseBlock.getName());
                    LLVMBuilder.getLlvmBuilder().removeBasicBlock(followBlock.getName());

                    Cond cond = (Cond) children.get(2);
                    cond.generateIRForCond(thenBlock, elseBlock);

                    LLVMBuilder.getLlvmBuilder().addBasicBlock(thenBlock);
                    LLVMBuilder.getLlvmBuilder().setCurBlock(thenBlock);
                    children.get(4).generateIR();

                    new JumpInstr(null, followBlock);

                    LLVMBuilder.getLlvmBuilder().addBasicBlock(elseBlock);
                    LLVMBuilder.getLlvmBuilder().setCurBlock(elseBlock);
                    children.get(6).generateIR();

                    new JumpInstr(null, followBlock);

                    LLVMBuilder.getLlvmBuilder().addBasicBlock(followBlock);
                    LLVMBuilder.getLlvmBuilder().setCurBlock(followBlock);
                } else {
                    BasicBlock followBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                    Cond cond = (Cond) children.get(2);
                    cond.generateIRForCond(thenBlock, followBlock);
                    LLVMBuilder.getLlvmBuilder().removeBasicBlock(thenBlock.getName());
                    LLVMBuilder.getLlvmBuilder().removeBasicBlock(followBlock.getName());

                    LLVMBuilder.getLlvmBuilder().addBasicBlock(thenBlock);
                    LLVMBuilder.getLlvmBuilder().setCurBlock(thenBlock);
                    children.get(4).generateIR();

                    new JumpInstr(null, followBlock);

                    LLVMBuilder.getLlvmBuilder().addBasicBlock(followBlock);
                    LLVMBuilder.getLlvmBuilder().setCurBlock(followBlock);
                }
                return null;
            } else {
                super.generateIR();
                return null;
            }
        } else if (children.get(0).getType() == SyntaxType.Block) {
            return children.get(0).generateIR();
        } else {
            return children.get(0).generateIR();
        }
        return null;
    }
}
