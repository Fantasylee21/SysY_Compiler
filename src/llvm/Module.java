package llvm;

import backend.MipsBuilder;
import backend.objInstr.ObjCommentInstr;
import backend.objInstr.ObjLabelInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjSyscallInstr;
import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;
import backend.register.Register;
import llvm.type.OtherType;

import java.util.ArrayList;

public class Module extends Value {
    private ArrayList<GlobalValue> globalValues;
    private ArrayList<Function> functions;
    private ArrayList<GlobalVariable> globalVariables;
    private ArrayList<PrintString> printStrings;
    private String IOFunctionsDef = """
            declare i32 @getint()
            declare i32 @getchar()
            declare void @putint(i32)
            declare void @putch(i32)
            declare void @putstr(i8*)
            """;

    public Module() {
        super(OtherType.getModule(), "CompUnit");
        this.globalValues = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.printStrings = new ArrayList<>();
        this.globalVariables = new ArrayList<>();
    }

    public ArrayList<GlobalValue> getGlobalValues() {
        return globalValues;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ArrayList<GlobalVariable> getGlobalVariables() {
        return globalVariables;
    }

    public ArrayList<PrintString> getPrintStrings() {
        return printStrings;
    }

    public void addGlobalVariables(GlobalVariable globalValue) {
        globalValues.add(globalValue);
        globalVariables.add(globalValue);
    }

    public void addFunction(Function function) {
        globalValues.add(function);
        functions.add(function);
    }

    public void addPrintString(PrintString printString) {
        globalValues.add(printString);
        printStrings.add(printString);
    }


    @Override
    public String toString() {
        StringBuilder code = new StringBuilder(IOFunctionsDef);
        code.append("\n");
        for (GlobalVariable globalVariable : globalVariables) {
            code.append(globalVariable.toString()).append("\n");
        }
        code.append("\n");
        for (PrintString printString : printStrings) {
            code.append(printString.toString()).append("\n");
        }
        code.append("\n");
        for (Function function : functions) {
            code.append(function.toString()).append("\n");
        }
        return code.toString();
    }

    public void generateMips() {
        for (PrintString printString : printStrings) {
            printString.generateMips();
        }
        for (GlobalVariable globalVariable : globalVariables) {
            globalVariable.generateMips();
        }
        new ObjCommentInstr("Jump to main");
        new ObjJumpInstr(JumpType.JAL, "main");

        new ObjCommentInstr("Exit program");
        new ObjJumpInstr(JumpType.JAL, "exit");

        int maxFuncParamSize = 0;
        for (Function function : functions) {
            int paramSize = function.getArguments().size();
            if (paramSize > maxFuncParamSize) {
                maxFuncParamSize = paramSize;
            }
        }
        maxFuncParamSize *= 4;
        if (maxFuncParamSize < 0) {
            maxFuncParamSize = 0;
        }
        MipsBuilder.getMipsBuilder().setMaxFuncParamSize(maxFuncParamSize);

        for (Function function : functions) {
            function.generateMips();
        }
        new ObjLabelInstr("exit");
        new ObjLiInstr(Register.get$v0(), 10);
        new ObjSyscallInstr();
    }
}
