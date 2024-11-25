package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.TokenType;
import llvm.Constant;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.midInstr.ZextInstr;
import llvm.midInstr.icmp.IcmpInstr;
import llvm.midInstr.icmp.IcmpOp;
import llvm.type.Int32Type;
import llvm.type.LLVMEnumType;

import java.util.ArrayList;

// EqExp ==> RelExp {('==' | '!=') RelExp}
public class EqExp extends Node {
    public EqExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void print() {
        int cnt = children.size();
        for (Node child : children) {
            child.print();
            if (cnt > 2 && child.getType() == SyntaxType.RelExp) {
                System.out.println("<" + type.toString() + ">");
            }
            cnt--;
        }
        System.out.println("<" + type.toString() + ">");
    }

    @Override
    public Value generateIR() {
        Value operand1 = children.get(0).generateIR();
        Value operand2 = null;
        if (children.size() == 1) {
            if (operand1.getType().getType() == LLVMEnumType.Int32Type) {
                operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.NE, operand1, new Constant(0));
            }
            return operand1;
        } else {
            for (int i = 1; i < children.size(); i += 2) {
                if (!(operand1.getType().getType() == LLVMEnumType.Int32Type)) {
                    operand1 = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), operand1, Int32Type.getInstance());
                }
                operand2 = children.get(i + 1).generateIR();
                if (!(operand2.getType().getType() == LLVMEnumType.Int32Type)) {
                    operand2 = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), operand2, Int32Type.getInstance());
                }
                TokenNode op = (TokenNode) children.get(i);
                if (op.getToken().getType() == TokenType.EQL) {
                    operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.EQ, operand1, operand2);
                } else {
                    operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.NE, operand1, operand2);
                }
            }
            return operand1;
        }
    }
}
