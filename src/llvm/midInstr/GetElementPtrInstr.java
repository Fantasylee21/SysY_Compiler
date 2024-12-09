package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.ObjLaInstr;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.riCalculate.RICalculateType;
import backend.objInstr.rrCalculate.ObjRRCalculateInstr;
import backend.objInstr.rrCalculate.RRCalculateType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.GlobalVariable;
import llvm.Value;
import llvm.type.ArrayType;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;
import llvm.type.PointerType;


public class GetElementPtrInstr extends MidInstr {
    public GetElementPtrInstr(String name, Value pointer, LLVMType elementType , Value offset) {
        super(new PointerType(elementType), name, MidInstrType.GETELEMENTPTR);
        addOperand(pointer);
        addOperand(offset);
    }

    @Override
    public boolean isDef() {
        return true;
    }

    @Override
    public String toString() {
        Value pointer = operands.get(0);
        Value offset = operands.get(1);
        PointerType pointerType = (PointerType) pointer.getType();
        LLVMType elementType = pointerType.getTargetType();

        if (elementType.getType() == LLVMEnumType.ArrayType) {
            ArrayType arrayType = (ArrayType) elementType;
            if (arrayType.getElementType().getType() == LLVMEnumType.Int8Type) {
                return name + " = getelementptr inbounds " + elementType + ", " + pointerType + " " + pointer.getName() + ", i32 0, i32 " + offset.getName();
            } else {
                return name + " = getelementptr inbounds " + elementType + ", " + pointerType + " " + pointer.getName() + ", i32 0, i32 " + offset.getName();
            }
        } else if (elementType.getType() == LLVMEnumType.Int8Type) {
            return name + " = getelementptr inbounds " + elementType + ", " + pointerType + " " + pointer.getName() + ", i32 " + offset.getName();
        } else {
            return name + " = getelementptr inbounds " + elementType + ", " + pointerType + " " + pointer.getName() + ", i32 " + offset.getName();
        }
    }

    @Override
    public void generateMips() {

        Register pointer = MipsBuilder.getMipsBuilder().getRegister(operands.get(0));
        if (operands.get(0) instanceof GlobalVariable) {
            pointer = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(operands.get(0), pointer);
            new ObjLaInstr(pointer, operands.get(0).getName().substring(1));
        }

        PointerType pointerType = (PointerType) operands.get(0).getType();
        LLVMType elementType = pointerType.getTargetType();
        LLVMType ansType;
        if (elementType.getType() == LLVMEnumType.ArrayType) {
            ArrayType arrayType = (ArrayType) elementType;
            ansType = arrayType.getElementType();
        } else {
            ansType = elementType;
        }

        Value offsetValue = operands.get(1);
        if (offsetValue instanceof Constant) {
            int offset = ((Constant) offsetValue).getValue();
            if (offset == 0) {
               MipsBuilder.getMipsBuilder().addRegisterAllocation(this, pointer);
            } else {
                Register ans = new Register(VirtualRegister.getVirtualRegister().getRegister());
                MipsBuilder.getMipsBuilder().addRegisterAllocation(this, ans);
//                if (ansType.getType() == LLVMEnumType.Int8Type && isGlobalVariable()) {
//                    new ObjRICalculate(RICalculateType.ADDI, ans, pointer, offset);
//                } else {
                    new ObjRICalculate(RICalculateType.ADDI, ans, pointer, offset * 4);
//                }
            }
        } else {
            Register offset = MipsBuilder.getMipsBuilder().getRegister(offsetValue);
            Register ans = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(this, ans);
//            if (ansType.getType() == LLVMEnumType.Int8Type && isGlobalVariable()) {
//                new ObjRRCalculateInstr(RRCalculateType.ADD, ans, pointer, offset);
//            } else {
                new ObjRICalculate(RICalculateType.SLL, Register.get$v1(), offset, 2);
                new ObjRRCalculateInstr(RRCalculateType.ADDU, ans, pointer, Register.get$v1());
//            }
        }

    }

    public boolean isGlobalVariable() {
        return operands.get(0) instanceof GlobalVariable;
    }
}
