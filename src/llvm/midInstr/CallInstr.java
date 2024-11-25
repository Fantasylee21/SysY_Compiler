package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.ObjCommentInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjMoveInstr;
import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.Function;
import llvm.Value;
import llvm.type.Int8Type;
import llvm.type.VoidType;

import java.util.ArrayList;
import java.util.List;

public class CallInstr extends MidInstr {
    public CallInstr(String name, Function function, ArrayList<Value> arguments) {
        super(function.getReturnType(), name, MidInstrType.CALL);
        addOperand(function);
        for (Value argument : arguments) {
            addOperand(argument);
        }
    }

    public Function getTargetFunc() {
        return (Function) operands.get(0);
    }

    public List<Value> getArguments() {
        return operands.subList(1, operands.size());
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type instanceof VoidType) {
            sb.append("call void ");
        } else if (type instanceof Int8Type) {
            sb.append(name).append(" = ");
            sb.append("call i8 ");
        } else {
            sb.append(name).append(" = ");
            sb.append("call i32 ");
        }
        sb.append(getTargetFunc().getName()).append("(");
        for (int i = 1; i < operands.size(); i++) {
            sb.append(operands.get(i).getType().toString()).append(" ").append(operands.get(i).getName());
            if (i < operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void generateMips() {
        new ObjCommentInstr("call start");
        for (int i = 1; i < operands.size(); i ++) {
            Register register = MipsBuilder.getMipsBuilder().getRegister(operands.get(i));
//            if (i == 1) {
//                if (operands.get(i) instanceof Constant) {
//                    new ObjLiInstr(Register.get$a0(), ((Constant) operands.get(i)).getValue());
//                } else {
//                    new ObjMoveInstr(Register.get$a0(), register);
//                }
//            } else if (i == 2) {
//                if (operands.get(i) instanceof Constant) {
//                    new ObjLiInstr(Register.get$a1(), ((Constant) operands.get(i)).getValue());
//                } else {
//                    new ObjMoveInstr(Register.get$a1(), register);
//                }
//            } else if (i == 3) {
//                if (operands.get(i) instanceof Constant) {
//                    new ObjLiInstr(Register.get$a2(), ((Constant) operands.get(i)).getValue());
//                } else {
//                    new ObjMoveInstr(Register.get$a2(), register);
//                }
//            } else if (i == 4) {
//                if (operands.get(i) instanceof Constant) {
//                    new ObjLiInstr(Register.get$a3(), ((Constant) operands.get(i)).getValue());
//                } else {
//                    new ObjMoveInstr(Register.get$a3(), register);
//                }
//            } else {
                if (operands.get(i) instanceof Constant) {
                    register = new Register(VirtualRegister.getVirtualRegister().getRegister());
                    new ObjLiInstr(register, ((Constant) operands.get(i)).getValue());
                }
//                TODO: i - 5 实现不存前四个参数
                if (operands.get(i).getType() instanceof Int8Type) {
                    new ObjStoreInstr(StoreType.SB, register, Register.get$sp(), (i - 1) * 4);
                } else {
                    new ObjStoreInstr(StoreType.SW, register, Register.get$sp(), (i - 1) * 4);
                }
                MipsBuilder.getMipsBuilder().addStackFrameValue((i - 1) * 4 ,register);
//            }
        }
        new ObjStoreInstr(StoreType.SW, Register.get$ra(), Register.get$sp(), MipsBuilder.getMipsBuilder().getMaxFuncParamSize());
//        for (int i = 0; i < 8; i++) {
//            Register register = Register.get$s(i);
//            int offset = MipsBuilder.getMipsBuilder().getMaxFuncParamSize() + 4 * i + 4;
//            new ObjStoreInstr(StoreType.SW, register, Register.get$sp(), offset);
//            MipsBuilder.getMipsBuilder().addStackFrameValue(offset, register);
//        }

        new ObjJumpInstr(JumpType.JAL, getTargetFunc().getName().substring(1));

        Register retValue = new Register(VirtualRegister.getVirtualRegister().getRegister());
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, retValue);

        new ObjMoveInstr(retValue, Register.get$v0());

//        for (int i = 0; i < 8; i++) {
//            Register register = Register.get$s(i);
//            new ObjLoadInstr(LoadType.LW, register, Register.get$sp(), MipsBuilder.getMipsBuilder().getMaxFuncParamSize() + 4 * i + 4);
//        }

        new ObjLoadInstr(LoadType.LW, Register.get$ra(), Register.get$sp(), MipsBuilder.getMipsBuilder().getMaxFuncParamSize());

        new ObjCommentInstr("call end");
    }
}
