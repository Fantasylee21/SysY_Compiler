package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.TokenType;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.instr.ZextInstr;
import llvm.instr.icmp.IcmpInstr;
import llvm.instr.icmp.IcmpOp;
import llvm.type.Int32Type;
import llvm.type.LLVMEnumType;

import java.util.ArrayList;
// RelExp ==> AddExp {('<' | '>' | '<=' | '>=') AddExp}
public class RelExp extends Node {
    public RelExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void print() {
        int cnt = children.size();
        for (Node child : children) {
            child.print();
            if (cnt > 2 && child.getType() == SyntaxType.AddExp) {
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
                if (op.getToken().getType() == TokenType.LSS) {
                    // operand1 < operand2
                    operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.SLT, operand1, operand2);
                } else if (op.getToken().getType() == TokenType.GRE) {
                    // operand1 > operand2
                    operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.SGT, operand1, operand2);
                } else if (op.getToken().getType() == TokenType.LEQ) {
                    // operand1 <= operand2
                    operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.SLE, operand1, operand2);
                } else {
                    // operand1 >= operand2
                    operand1 = new IcmpInstr(LLVMBuilder.getLlvmBuilder().getVarName(), IcmpOp.SGE, operand1, operand2);
                }
            }
            return operand1;
        }
    }
}
