package backend;

import backend.objInstr.ObjInstr;
import backend.optimize.RegAllocator;
import backend.register.Register;
import llvm.Value;

import java.util.HashMap;
import java.util.LinkedList;

/*
    |-------------------------|
    |----------ext------------|
    |-------------------------|
    |---------local-----------|
    |-------------------------|
    |----------save-----------|
    |-------------------------|
    |-----------ra------------|
    |-------------------------|
    |----------para-----------|
    |-------------------------|
 */
public class MipsBuilder {
    private static MipsBuilder mipsBuilder = new MipsBuilder();
    private ObjFunction currentFunction;
    private ObjModule objModule;
    private LinkedList<HashMap<Value, Register>> registerStack;
    private RegAllocator regAllocator;

    private MipsBuilder() {
        currentFunction = null;
        objModule = new ObjModule();
        registerStack = new LinkedList<>();
    }

    public static MipsBuilder getMipsBuilder() {
        return mipsBuilder;
    }

    public ObjModule getObjModule(boolean isOpt) {
        regAllocator = new RegAllocator(objModule);
        regAllocator.allocReg();
        return objModule;
    }

    public ObjModule getObjModule() {
        return objModule;
    }

    public void setCurrentFunction(ObjFunction function) {
        currentFunction = function;
    }

    public ObjFunction getCurrentFunction() {
        return currentFunction;
    }

    public HashMap<Value, Register> getCurRegisterAllocation() {
        return registerStack.peek();
    }

    public void addRegisterAllocation(Value value, Register register) {
        getCurRegisterAllocation().put(value, register);
    }

    public Register getRegister(Value value) {
        Register register = getCurRegisterAllocation().get(value);
        if (register == null) {
            return null;
        } else {
            if (register.isRealRegister()) {
                return new Register(register.getRealRegister());
            } else {
                return new Register(register.getVirtualReg());
            }
        }
    }

    public void enterFunction(ObjFunction function) {
        currentFunction = function;
        registerStack.push(new HashMap<>());
    }

    public void exitFunction() {
        objModule.addFunction(currentFunction);
        currentFunction = null;
    }

    public void addDataInstr(ObjInstr instr) {
        objModule.addGlobalVariable(instr);
    }

    public void addTextInstr(ObjInstr instr) {
        currentFunction.addInstr(instr);
    }

    public void curOffsetUp(int offset) {
        currentFunction.curOffsetUp(offset);
    }

    public int getMaxFuncParamSize() {
        return currentFunction.getParamSize();
    }

    public int getCurOffset() {
        return currentFunction.getCurOffset();
    }


}
