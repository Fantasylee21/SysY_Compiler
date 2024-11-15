package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.initial.ArrayInitial;
import llvm.initial.Initial;
import llvm.initial.VarInitial;
import llvm.type.LLVMType;

import java.util.ArrayList;
// ConstInitVal ==> ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
public class ConstInitVal extends Node {
    public ConstInitVal(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    public Initial getInitial(LLVMType type, String name) {
        if (children.size() == 1) {
            if (children.get(0) instanceof ConstExp constExp) {
                int value = constExp.calcValue();
                return new VarInitial(type, name ,value);
            } else {
                ArrayList<Integer> values = new ArrayList<>();
                String str = ((TokenNode) children.get(0)).getToken().getValue();
                for (int i = 1; i < str.length() - 1; i++) {
                    if (str.charAt(i) == '\\') {
                        i++;
                        if (str.charAt(i) == '0') {
                            values.add(0);
                        } else if (str.charAt(i) == 'n') {
                            values.add(10);
                        } else if (str.charAt(i) == 'r') {
                            values.add(13);
                        } else if (str.charAt(i) == 't') {
                            values.add(9);
                        } else {
                            values.add((int) str.charAt(i));
                        }
                    } else {
                        values.add((int) str.charAt(i));
                    }
                }
                return new ArrayInitial(type, name, values);
            }
        } else {
            ArrayList<Integer> values = new ArrayList<>();
            for (Node child : getChildren()) {
                if (child instanceof ConstExp constExp) {
                    values.add(constExp.calcValue());
                }
            }
            return new ArrayInitial(type, name, values);
        }
    }
}
