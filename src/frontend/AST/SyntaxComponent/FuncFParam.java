package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.Value;

import java.util.ArrayList;
// FuncFParam ==> 'int'|'char' Ident ['[' ']']
public class FuncFParam extends Node {
    public FuncFParam(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}
