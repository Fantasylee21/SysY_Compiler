package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.TokenType;

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
    public int calcValue() {
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
}
