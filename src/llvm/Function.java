package llvm;

import backend.MipsBuilder;
import backend.ObjBlock;
import backend.ObjFunction;
import backend.objInstr.*;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.midInstr.RetInstr;
import llvm.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Function extends GlobalValue {
    private ArrayList<Value> arguments;
    private ArrayList<BasicBlock> basicBlocks;
    private LLVMType returnType;
    private int callMaxParam = 0;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> preMap;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> sucMap;
    private HashSet<Function> callFunc = new HashSet<>();
    private boolean isUsed = false;

    public Function(LLVMType returnType, String name) {
        super(OtherType.getFunction(), "@" + name);
        this.arguments = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
        this.returnType = returnType;
        LLVMBuilder.getLlvmBuilder().addFunction(this);
    }

    public void setCallMaxParam(int callMaxParam) {
        if (callMaxParam > this.callMaxParam) {
            this.callMaxParam = callMaxParam;
        }
    }

    public void setPreMap(HashMap<BasicBlock, ArrayList<BasicBlock>> preMap) {
        this.preMap = preMap;
    }

    public void setSucMap(HashMap<BasicBlock, ArrayList<BasicBlock>> sucMap) {
        this.sucMap = sucMap;
    }

    public void addCallFunc(Function function) {
        callFunc.add(function);
    }

    public HashSet<Function> getCallFunc() {
        return callFunc;
    }

    public void setUsed() {
        isUsed = true;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public HashMap<BasicBlock, ArrayList<BasicBlock>> getSucMap() {
        return sucMap;
    }

    public HashMap<BasicBlock, ArrayList<BasicBlock>> getPreMap() {
        return preMap;
    }

    public ArrayList<Value> getArguments() {
        return arguments;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public LLVMType getReturnType() {
        return returnType;
    }

    public void setArguments(ArrayList<Value> arguments) {
        this.arguments = arguments;
    }

    public void setBasicBlocks(ArrayList<BasicBlock> basicBlocks) {
        this.basicBlocks = basicBlocks;
    }

    public void removeBasicBlock(String name) {
        for (int i = 0; i < basicBlocks.size(); i++) {
            if (basicBlocks.get(i).getName().equals(name)) {
                basicBlocks.remove(i);
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(returnType).append(" ").append(name).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).toString());
            if (i != arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n");
        for (BasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock.toString()).append("\n");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("}\n");
        return sb.toString();
    }

    public void checkReturn() {
        BasicBlock lastBlock = LLVMBuilder.getLlvmBuilder().getCurBasicBlock();
        if (lastBlock.getInstructions().isEmpty() || !(lastBlock.getInstructions().get(lastBlock.getInstructions().size() - 1) instanceof RetInstr)) {
            new RetInstr(null, null);
        }
    }

    @Override
    public void generateMips() {
        ObjFunction objFunction = new ObjFunction(name.substring(1));
        objFunction.setLlvmFunction(this);
        int paramSize = callMaxParam * 4;
        objFunction.setParamSize(paramSize);
        MipsBuilder.getMipsBuilder().enterFunction(objFunction);

//        ObjBlock objBlock = new ObjBlock(name.substring(1) + "_entry_branch");
//        MipsBuilder.getMipsBuilder().getCurrentFunction().enterBlock(objBlock);

//
//        new ObjCommentInstr("load arguments " + name.substring(1));
//        for (int i = 0; i < arguments.size(); i++) {
//            if (i == 0) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a0());
//            } else if (i == 1) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a1());
//            } else if (i == 2) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a2());
//            } else if (i == 3) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a3());
//            } else {
//                Register register = new Register(VirtualRegister.getVirtualRegister().getRegister());
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), register);
//                if (arguments.get(i).getType().getType() == LLVMEnumType.Int8Type) {
//                    new ObjLoadInstr(LoadType.LB, register, Register.get$sp(), 4 * (i - 4));
//                } else {
//                    new ObjLoadInstr(LoadType.LW, register, Register.get$sp(), 4 * (i - 4));
//                }
//            }
//        }
//        new ObjCommentInstr("end arguments " + name.substring(1));

//        MipsBuilder.getMipsBuilder().getCurrentFunction().exitBlock();

        boolean first = true;
        for (BasicBlock basicBlock : basicBlocks) {
            ObjBlock objBlock = new ObjBlock(basicBlock.getName());
            MipsBuilder.getMipsBuilder().getCurrentFunction().enterBlock(objBlock);
            if (first) {
                first = false;
                new ObjCommentInstr("load arguments " + name.substring(1));
                for (int i = 0; i < arguments.size(); i++) {
                    if (i == 0) {
                        MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a0());
                    } else if (i == 1) {
                        MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a1());
                    } else if (i == 2) {
                        MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a2());
                    } else if (i == 3) {
                        MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a3());
                    } else {
                        Register register = new Register(VirtualRegister.getVirtualRegister().getRegister());
                        MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), register);
                        if (arguments.get(i).getType().getType() == LLVMEnumType.Int8Type) {
                            new ObjLoadInstr(LoadType.LB, register, Register.get$sp(), 4 * (i - 4));
                        } else {
                            new ObjLoadInstr(LoadType.LW, register, Register.get$sp(), 4 * (i - 4));
                        }
                    }
                }
                new ObjCommentInstr("end arguments " + name.substring(1));
            }
            basicBlock.generateMips();
            MipsBuilder.getMipsBuilder().getCurrentFunction().exitBlock();
        }

        MipsBuilder.getMipsBuilder().exitFunction();


    }
}
