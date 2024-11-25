package llvm;

import llvm.midInstr.MidInstr;

import java.util.HashMap;
import java.util.Stack;

public class LLVMBuilder {
    private static final LLVMBuilder llvmBuilder = new LLVMBuilder();
    private static int registerCounter = 0;
    private static int printStringCounter = 0;
    private static int branchCounter = 0;
    private Module module;
    private Function curFunction;
    private BasicBlock curBasicBlock;
    private HashMap<Function, String> varMap;
    private Stack<Loop> loopStack;

    public LLVMBuilder() {
        module = new Module();
        curFunction = null;
        curBasicBlock = null;
        varMap = new HashMap<>();
        loopStack = new Stack<>();
    }

    public static LLVMBuilder getLlvmBuilder() {
        return llvmBuilder;
    }

    public static String generateRegister() {
        return "%v" + registerCounter++;
    }

    public void resetRegisterCounter() {
        registerCounter = 0;
    }

    public static String generatePrintString() {
        if (printStringCounter == 0) {
            printStringCounter++;
            return "@.str";
        }
        return "@.str." + printStringCounter++;
    }

    public static String generateBranch() {
        return "branch" + branchCounter++;
    }

    public Module getModule() {
        return module;
    }

    public Function getCurFunction() {
        return curFunction;
    }

    public BasicBlock getCurBasicBlock() {
        return curBasicBlock;
    }

    public void setCurFunction(Function function) {
        curFunction = function;
    }

    public void setCurBlock(BasicBlock basicBlock) {
        curBasicBlock = basicBlock;
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        module.addGlobalVariables(globalVariable);
    }

    public void addFunction(Function function) {
        module.addFunction(function);
    }

    public void addPrintString(PrintString printString) {
        module.addPrintString(printString);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        curFunction.getBasicBlocks().add(basicBlock);
        basicBlock.setParentFunction(curFunction);
    }

    public void removeBasicBlock(String name) {
        curFunction.removeBasicBlock(name);
    }

    public void addInstruction(MidInstr instr) {
        curBasicBlock.addInstr(instr);
        instr.setParentBasicBlock(curBasicBlock);
    }

    public String getVarName(Function function) {
        String name = generateBranch();
        setCurFunction(function);
        varMap.put(function, name);
        return name;
    }

    public String getVarName() {
        String name = generateRegister();
        varMap.put(curFunction, name);
        return name;
    }

    public String getBranchName() {
        return generateBranch();
    }

    public String getPrintStringName() {
        return generatePrintString();
    }

    public Loop getCurLoop() {
        return loopStack.peek();
    }

    public Stack<Loop> getLoopStack() {
        return loopStack;
    }
}
