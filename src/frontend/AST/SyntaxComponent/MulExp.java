package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.TokenType;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.instr.Instr;
import llvm.instr.ZextInstr;
import llvm.instr.binaryOperatorTy.BinaryOp;
import llvm.instr.binaryOperatorTy.BinaryOperatorTyInstr;
import llvm.type.Int32Type;
import llvm.type.LLVMEnumType;

import java.util.ArrayList;
// MulExp ==> UnaryExp {('*' | '/' | '%') UnaryExp}
public class MulExp extends Node {
    public MulExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    @Override
    public void print() {
        int cnt = children.size();
        for (Node child : children) {
            child.print();
            if (cnt > 2 && child.getType() == SyntaxType.UnaryExp) {
                System.out.println("<" + type.toString() + ">");
            }
            cnt--;
        }
        System.out.println("<" + type.toString() + ">");
    }

    @Override
    public Integer calcValue() {
        int result = children.get(0).calcValue();
        int cnt = children.size();
        for (int i = 1; i < cnt; i += 2) {
            TokenNode op = (TokenNode) children.get(i);
            if (op.getToken().getType() == TokenType.MULT) {
                result *= children.get(i + 1).calcValue();
            } else if (op.getToken().getType() == TokenType.DIV) {
                result /= children.get(i + 1).calcValue();
            } else {
                result %= children.get(i + 1).calcValue();
            }
        }
        return result;
    }

    @Override
    public ExpValueType getExpValueType() {
        for (Node child : children) {
            if (child.getType() == SyntaxType.UnaryExp) {
                ExpValueType expValueType = child.getExpValueType();
                if (expValueType != ExpValueType.CONSTANT) {
                    return expValueType;
                }
            }
        }
        return ExpValueType.CONSTANT;
    }

    @Override
    public Value generateIR() {
        Value operand1 = children.get(0).generateIR();
        if (operand1.getType().getType() == LLVMEnumType.Int8Type || operand1.getType().getType() == LLVMEnumType.BoolType) {
            operand1 = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), operand1, Int32Type.getInstance());
        }
        if (children.size() == 1) {
            return operand1;
        }
        Value operand2 = null;
        Instr instr = null;
        int cnt = children.size();
        for (int i = 1; i < cnt; i += 2) {
            TokenNode op = (TokenNode) children.get(i);
            operand2 = children.get(i + 1).generateIR();
            if (operand2.getType().getType() == LLVMEnumType.Int8Type || operand1.getType().getType() == LLVMEnumType.BoolType) {
                operand2 = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), operand2, Int32Type.getInstance());
            }
            if (op.getToken().getType() == TokenType.MULT) {
                instr = new BinaryOperatorTyInstr(LLVMBuilder.getLlvmBuilder().getVarName(), BinaryOp.MUL, operand1, operand2);
            } else if (op.getToken().getType() == TokenType.DIV) {
                instr = new BinaryOperatorTyInstr(LLVMBuilder.getLlvmBuilder().getVarName(), BinaryOp.SDIV, operand1, operand2);
            } else {
                instr = new BinaryOperatorTyInstr(LLVMBuilder.getLlvmBuilder().getVarName(), BinaryOp.SREM, operand1, operand2);
            }
            operand1 = instr;
        }
        return operand1;
    }
}
