package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

// CompUnit  ==>  {ConstDecl | VarDecl } {FuncDef} MainFuncDef
public class CompUnit extends Node {
    public CompUnit(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}
