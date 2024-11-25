package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.Constant;
import llvm.Value;
import llvm.initial.ArrayInitial;
import llvm.initial.Initial;
import llvm.initial.VarInitial;
import llvm.type.LLVMType;

import java.util.ArrayList;
// InitVal ==> Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
public class InitVal extends Node {
    public InitVal(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    public Initial getInitial(LLVMType type, String name) {
        if (children.size() == 1) {
            if (children.get(0) instanceof Exp exp) {
                int value = exp.calcValue();
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
                if (child instanceof Exp exp) {
                    values.add(exp.calcValue());
                }
            }
            return new ArrayInitial(type, name, values);
        }
    }

    public ArrayList<Value> generateIRList() {
        ArrayList<Value> values = new ArrayList<>();
        for (Node child : getChildren()) {
            if (child instanceof Exp exp) {
                values.add(exp.generateIR());
            } else if (child instanceof TokenNode tokenNode) {
                String str = tokenNode.getToken().getValue();
                for (int i = 1; i < str.length() - 1; i++) {
                    //                    values.add(new Constant(str.charAt(i)));
                    if (str.charAt(i) == '\\') {
                        i++;
                        if (str.charAt(i) == '0') {
                            values.add(new Constant(0));
                        } else if (str.charAt(i) == 'n') {
                            values.add(new Constant(10));
                        } else if (str.charAt(i) == 'r') {
                            values.add(new Constant(13));
                        } else if (str.charAt(i) == 't') {
                            values.add(new Constant(9));
                        } else {
                            values.add(new Constant(str.charAt(i)));
                        }
                    } else {
                        values.add(new Constant(str.charAt(i)));
                    }
                }
            }
        }
        return values;
    }

}
