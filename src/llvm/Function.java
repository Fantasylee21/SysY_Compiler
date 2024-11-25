package llvm;

import backend.MipsBuilder;
import backend.objInstr.*;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.RealRegister;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.midInstr.RetInstr;
import llvm.type.*;

import java.util.ArrayList;

public class Function extends GlobalValue {
    private ArrayList<Value> arguments;
    private ArrayList<BasicBlock> basicBlocks;
    private LLVMType returnType;

    public Function(LLVMType returnType, String name) {
        super(OtherType.getFunction(), "@" + name);
        this.arguments = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
        this.returnType = returnType;
        LLVMBuilder.getLlvmBuilder().addFunction(this);
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
        MipsBuilder.getMipsBuilder().enterFunction(this);
        new ObjCommentInstr("enter function " + name.substring(1));
        new ObjLabelInstr(name.substring(1));

        new ObjCommentInstr("load arguments " + name.substring(1));
        for (int i = 0; i < arguments.size(); i++) {
//            if (i == 0) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a0());
//            } else if (i == 1) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a1());
//            } else if (i == 2) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a2());
//            } else if (i == 3) {
//                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), Register.get$a3());
//            } else {
                Register register = new Register(VirtualRegister.getVirtualRegister().getRegister());
                MipsBuilder.getMipsBuilder().addRegisterAllocation(arguments.get(i), register);
                if (arguments.get(i).getType().getType() == LLVMEnumType.Int8Type) {
                    new ObjLoadInstr(LoadType.LB, register, Register.get$sp(), 4 * (i));
                } else {
                    new ObjLoadInstr(LoadType.LW, register, Register.get$sp(), 4 * (i));
                }
//            }
        }
        new ObjCommentInstr("end arguments " + name.substring(1));
//        if (!name.equals("@main")) {
//            new ObjNopInstr();
//        }

        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.generateMips();
        }

        MipsBuilder.getMipsBuilder().exitFunction();
//        new ObjCommentInstr("return function " + name.substring(1));
//        if (name.equals("@main")) {
//            new ObjLiInstr(Register.get$v0(), 10);
//            new ObjSyscallInstr();
//        } else {
//            new ObjJRInstr(Register.get$ra());
//        }


    }
}
