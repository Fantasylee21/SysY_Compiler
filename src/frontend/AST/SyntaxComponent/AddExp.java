package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.TokenType;

import java.util.ArrayList;

// AddExp ==> MulExp {('+' | '-') MulExp}
public class AddExp extends Node {
    public AddExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void print() {
        int cnt = children.size();
        for (Node child : children) {
            child.print();
            if (cnt > 2 && child.getType() == SyntaxType.MulExp) {
                System.out.println("<" + type.toString() + ">");
            }
            cnt--;
        }
        System.out.println("<" + type.toString() + ">");
    }

    @Override
    public int calcValue() {
        int result = children.get(0).calcValue();
        int cnt = children.size();
        for (int i = 1; i < cnt; i += 2) {
            TokenNode op = (TokenNode) children.get(i);
            if (op.getToken().getType() == TokenType.PLUS) {
                result += children.get(i + 1).calcValue();
            } else {
                result -= children.get(i + 1).calcValue();
            }
        }
        return result;
    }

    @Override
    public ExpValueType getExpValueType() {
        for (Node child : children) {
            if (child.getType() == SyntaxType.MulExp) {
                ExpValueType expValueType = child.getExpValueType();
                if (expValueType != ExpValueType.CONSTANT) {
                    return expValueType;
                }
            }
        }
        return ExpValueType.CONSTANT;
    }
}
