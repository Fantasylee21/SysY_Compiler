package frontend.AST;

import java.util.ArrayList;

public class Node {
    private final int startLine;
    private final int endLine;
    protected final SyntaxType type;
    protected final ArrayList<Node> children;

    public Node(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.type = type;
        this.children = children;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public SyntaxType getType() {
        return type;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void print() {
        for (Node child : children) {
            child.print();
        }
        System.out.println("<" + type.toString() + ">");
    }

}
