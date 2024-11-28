package backend;

import backend.objInstr.ObjInstr;
import backend.register.Register;
import llvm.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ObjModule {
    private ArrayList<ObjFunction> functions;
    private ArrayList<ObjInstr> globalVariables;
    private ObjFunction mainFunction;

    public ObjModule() {
        functions = new ArrayList<ObjFunction>();
        globalVariables = new ArrayList<ObjInstr>();
        mainFunction = null;
    }

    public void addFunction(ObjFunction function) {
        functions.add(function);
    }

    public void setMainFunction(ObjFunction mainFunction) {
        this.mainFunction = mainFunction;
    }

    public ArrayList<ObjFunction> getFunctions() {
        return functions;
    }

    public ObjFunction getMainFunction() {
        return mainFunction;
    }

    public void addGlobalVariable(ObjInstr instr) {
        globalVariables.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".text\n");
        for (ObjInstr instr : globalVariables) {
            sb.append(instr.toString()).append("\n");
        }
        sb.append("\n.data\n");
        sb.append("\tjal main\n");
        sb.append("\tjal exit\n");
        sb.append("\n");
        for (ObjFunction function : functions) {
            sb.append(function.toString()).append("\n");
        }
        sb.append("\n");
        sb.append("exit:\n");
        sb.append("\tli $v0, 10\n");
        sb.append("\tsyscall\n");
        return sb.toString();
    }
}
