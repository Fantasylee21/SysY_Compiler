package backend;

import backend.objInstr.*;
import backend.objInstr.branch.ObjBranchInstr;
import backend.objInstr.dm.ObjDmInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.move.MoveType;
import backend.objInstr.move.ObjMoveHLInstr;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.riCalculate.RICalculateType;
import backend.objInstr.rrCalculate.ObjRRCalculateInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.RealRegister;
import backend.register.Register;
import llvm.Function;
import llvm.Value;

import java.util.*;
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
    private final static MipsBuilder mipsBuilder = new MipsBuilder();

    private ArrayList<ObjInstr> dataSegment;
    private ArrayList<ObjInstr> textSegment;
    private Function curFunction;
    private int curOffset = 0;
    private HashMap<String, Integer> Fun2Offset = new HashMap<>();
    private LinkedList<HashMap<Value, Register>> registerStack = new LinkedList<>();
    private LinkedList<HashMap<Integer, Register>> stackFrame = new LinkedList<>();
    private int maxFuncParamSize = 0;

    private MipsBuilder() {
        dataSegment = new ArrayList<>();
        textSegment = new ArrayList<>();
        curFunction = null;
        registerStack.push(new HashMap<>());
        stackFrame.push(new HashMap<>());
    }

    public static MipsBuilder getMipsBuilder() {
        return mipsBuilder;
    }

    public int getCurOffset() {
        return curOffset;
    }

    public Function getCurrentFunction() {
        return curFunction;
    }

    public int getMaxFuncParamSize() {
        return maxFuncParamSize;
    }

    public void setCurOffset(int curOffset) {
        this.curOffset = curOffset;
    }

    public void setMaxFuncParamSize(int size) {
        maxFuncParamSize = size;
    }

    public void addDataInstr(ObjInstr instr) {
        dataSegment.add(instr);
    }

    public void addTextInstr(ObjInstr instr) {
        textSegment.add(instr);
    }

    public HashMap<Value, Register> getCurRegisterAllocation() {
        return registerStack.peek();
    }

    public void addRegisterAllocation(Value value, Register register) {
        getCurRegisterAllocation().put(value, register);
    }

    public void enterFunction(Function function) {
        curFunction = function;
        registerStack.push(new HashMap<>());
        addStackFrame();
//        curOffset = 4 + 32 + maxFuncParamSize;
        curOffset = 4 + maxFuncParamSize;
    }

    public void exitFunction() {
        Fun2Offset.put(curFunction.getName().substring(1), curOffset);
        curFunction = null;
    }

    public HashMap<Integer, Register> getCurStackFrame() {
        return stackFrame.peek();
    }

    public void addStackFrame() {
        stackFrame.push(new HashMap<>());
    }

    public void removeStackFrame() {
        stackFrame.pop();
    }

    public void curOffsetUp(int offset) {
        curOffset += offset;
    }

    public void addStackFrameValue(Register register) {
        getCurStackFrame().put(curOffset, register);
        curOffsetUp(4);
    }

    public void addStackFrameValue(int offset, Register register) {
        getCurStackFrame().put(curOffset, register);
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

    public Register getStackFrameValue(int offset) {
        return getCurStackFrame().get(curOffset + offset);
    }

    public Integer getStackFrameOffset(Register register) {
        for (Integer offset : getCurStackFrame().keySet()) {
            if (getCurStackFrame().get(offset) == register) {
                return offset;
            }
        }
        return null;
    }

    public void alignStackFrame() {
        curOffset = (curOffset + 3) / 4 * 4;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        for (ObjInstr instr : dataSegment) {
            sb.append(instr.toString()).append("\n");
        }
        sb.append("\n.text\n\n");
        for (ObjInstr instr : textSegment) {
            sb.append(instr.toString()).append("\n\t");
        }
        return sb.toString();
    }
    
    private ArrayList<HashMap<Integer, ObjInstr>> toAdd = new ArrayList<>();
    
//    给toadd添加put方法
    public void toAddPut(int index, ObjInstr instr) {
        toAdd.add(new HashMap<>());
        toAdd.get(toAdd.size() - 1).put(index, instr);
    }

    public void setFuncSize() {
        int funcSize = 0;
        boolean startEditArguments = false;
        for (int i = 0; i < textSegment.size(); i++) {
            ObjInstr instr = textSegment.get(i);
            if (instr instanceof ObjCommentInstr commentInstr) {
                if (commentInstr.getComment().startsWith("enter function")) {
                    String funcName = commentInstr.getComment().substring(15);
                    funcSize = Fun2Offset.get(funcName);
                    toAddPut(i + 2, new ObjRICalculate(RICalculateType.SUBU, Register.get$sp(), Register.get$sp(), funcSize, true));
                } else if (commentInstr.getComment().startsWith("return function")) {
                    String funcName = commentInstr.getComment().substring(16);
                    funcSize = Fun2Offset.get(funcName);
                    toAddPut(i, new ObjRICalculate(RICalculateType.ADDU, Register.get$sp(), Register.get$sp(), funcSize, true));
                } else if (commentInstr.getComment().startsWith("load arguments")) {
                    startEditArguments = true;
                } else if (commentInstr.getComment().startsWith("end arguments")) {
                    startEditArguments = false;
                }
            } else if (instr instanceof ObjLoadInstr loadInstr && startEditArguments) {
                loadInstr.setOffset(loadInstr.getOffset() + funcSize);
            }
        }
        for (int i = toAdd.size() - 1; i >= 0; i--) {
            for (Integer index : toAdd.get(i).keySet()) {
                textSegment.add(index, toAdd.get(i).get(index));
            }
        }
        toAdd.clear();
    }

//    从大到小
    private TreeMap<Integer, String> frame = new TreeMap<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    });

    public int getOffset(Register register) {
        for (Integer offset : frame.keySet()) {
            if (frame.get(offset).equals(register.getVirtualReg())) {
                return offset;
            }
        }
        return -1;
    }

    public void allocateRegister() {
        int funCount = 0;
        String funcName = "";
        for (ObjInstr instr : textSegment) {
            int a = 1;
            if (instr instanceof ObjCommentInstr) {
                if (((ObjCommentInstr) instr).getComment().startsWith("enter function")) {
                    if (funCount != 0) {
                        Fun2Offset.put(funcName, curOffset);
                    }
                    funcName = ((ObjCommentInstr) instr).getComment().substring(15);
                    funCount++;
                    curOffset = Fun2Offset.get(funcName);
                }
            } else if (instr instanceof ObjBranchInstr) {
                Register rs = ((ObjBranchInstr) instr).getRs();
                Register rt = ((ObjBranchInstr) instr).getRt();
                if (!rs.isRealRegister()) {
                    int rsOffset = getOffset(rs);
                    if (rsOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t0(), Register.get$sp(), rsOffset, true));
                    }
                    rs.setRealRegister(RealRegister.T0);
                }
                if (!rt.isRealRegister()) {
                    int rtOffset = getOffset(rt);
                    if (rtOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), rtOffset, true));
                    }
                    rt.setRealRegister(RealRegister.T1);
                }
            } else if (instr instanceof ObjDmInstr) {
                Register rs = ((ObjDmInstr) instr).getRs();
                Register rt = ((ObjDmInstr) instr).getRt();
                if (!rs.isRealRegister()) {
                    int rsOffset = getOffset(rs);
                    if (rsOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t0(), Register.get$sp(), rsOffset, true));
                    }
                    rs.setRealRegister(RealRegister.T0);
                }
                if (!rt.isRealRegister()) {
                    int rtOffset = getOffset(rt);
                    if (rtOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), rtOffset, true));
                    }
                    rt.setRealRegister(RealRegister.T1);
                }
            } else if (instr instanceof ObjLoadInstr) {
                Register rt = ((ObjLoadInstr) instr).getRt();
                Register base = ((ObjLoadInstr) instr).getBase();
                LoadType loadType = ((ObjLoadInstr) instr).getLoadType();
                if (!base.isRealRegister()) {
                    int baseOffset = getOffset(base);
                    if (baseOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), baseOffset, true));
                    }
                    base.setRealRegister(RealRegister.T1);
                }
                if (!rt.isRealRegister()) {
                    toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                    frame.put(curOffset, rt.getVirtualReg());
                    addStackFrameValue(Register.get$t0());
                    rt.setRealRegister(RealRegister.T0);
                }
            } else if (instr instanceof ObjMoveHLInstr) {
                Register rd = ((ObjMoveHLInstr) instr).getRd();
                MoveType moveType = ((ObjMoveHLInstr) instr).getMoveType();
                if (!rd.isRealRegister()) {
                    if (moveType == MoveType.MFHI || moveType == MoveType.MFLO) {
                        toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                        frame.put(curOffset, rd.getVirtualReg());
                        addStackFrameValue(Register.get$t0());
                        rd.setRealRegister(RealRegister.T0);
                    } else {
                        int rdOffset = getOffset(rd);
                        if (rdOffset != -1) {
                            toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t0(), Register.get$sp(), rdOffset, true));
                        }
                        rd.setRealRegister(RealRegister.T0);
                    }
                }
            } else if (instr instanceof ObjRICalculate) {
                Register rs = ((ObjRICalculate) instr).getRs();
                if (!rs.isRealRegister()) {
                    int rsOffset = getOffset(rs);
                    if (rsOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), rsOffset, true));
                    }
                    rs.setRealRegister(RealRegister.T1);
                }
                Register rt = ((ObjRICalculate) instr).getRt();
                if (!rt.isRealRegister()) {
                    toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                    frame.put(curOffset, rt.getVirtualReg());
                    addStackFrameValue(Register.get$t0());
                    rt.setRealRegister(RealRegister.T0);
                }
            } else if (instr instanceof ObjRRCalculateInstr) {
                Register rs = ((ObjRRCalculateInstr) instr).getRs();
                if (!rs.isRealRegister()) {
                    int rsOffset = getOffset(rs);
                    if (rsOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), rsOffset, true));
                    }
                    rs.setRealRegister(RealRegister.T1);
                }
                Register rt = ((ObjRRCalculateInstr) instr).getRt();
                if (!rt.isRealRegister()) {
                    int rtOffset = getOffset(rt);
                    if (rtOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t0(), Register.get$sp(), rtOffset, true));
                    }
                    rt.setRealRegister(RealRegister.T0);
                }
                Register rd = ((ObjRRCalculateInstr) instr).getRd();
                if (!rd.isRealRegister()) {
                    toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                    frame.put(curOffset, rd.getVirtualReg());
                    addStackFrameValue(Register.get$t0());
                    rd.setRealRegister(RealRegister.T0);
                }
            } else if (instr instanceof ObjMoveInstr) {
                Register src = ((ObjMoveInstr) instr).getSrc();
                if (!src.isRealRegister()) {
                    int srcOffset = getOffset(src);
                    if (srcOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), srcOffset, true));
                    }
                    src.setRealRegister(RealRegister.T1);
                }
                Register dst = ((ObjMoveInstr) instr).getDst();
                if (!dst.isRealRegister()) {
                    toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                    frame.put(curOffset, dst.getVirtualReg());
                    addStackFrameValue(Register.get$t0());
                    dst.setRealRegister(RealRegister.T0);
                }
            } else if (instr instanceof ObjStoreInstr) {
                Register rt = ((ObjStoreInstr) instr).getRt();
                if (!rt.isRealRegister()) {
                    int rtOffset = getOffset(rt);
                    if (rtOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t0(), Register.get$sp(), rtOffset, true));
                    }
                    rt.setRealRegister(RealRegister.T0);
                }
                Register base = ((ObjStoreInstr) instr).getBase();
                if (!base.isRealRegister()) {
                    int baseOffset = getOffset(base);
                    if (baseOffset != -1) {
                        toAddPut(textSegment.indexOf(instr), new ObjLoadInstr(LoadType.LW, Register.get$t1(), Register.get$sp(), baseOffset, true));
                    }
                    base.setRealRegister(RealRegister.T1);
                }
            } else if (instr instanceof ObjLaInstr) {
                Register target = ((ObjLaInstr) instr).getTarget();
                if (!target.isRealRegister()) {
                    toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                    frame.put(curOffset, target.getVirtualReg());
                    addStackFrameValue(Register.get$t0());
                    target.setRealRegister(RealRegister.T0);
                }
            } else if (instr instanceof ObjLiInstr) {
                Register target = ((ObjLiInstr) instr).getTarget();
                if (!target.isRealRegister()) {
                    toAddPut(textSegment.indexOf(instr) + 1, new ObjStoreInstr(StoreType.SW, Register.get$t0(), Register.get$sp(), curOffset, true));
                    frame.put(curOffset, target.getVirtualReg());
                    addStackFrameValue(Register.get$t0());
                    target.setRealRegister(RealRegister.T0);
                }
            }
        }
        Fun2Offset.put(funcName, curOffset);
        for (int i = toAdd.size() - 1; i >= 0; i--) {
            for (Integer index : toAdd.get(i).keySet()) {
                textSegment.add(index, toAdd.get(i).get(index));
            }
        }
        toAdd.clear();
    }


}
