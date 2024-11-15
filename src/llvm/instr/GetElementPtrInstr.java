package llvm.instr;

import llvm.Value;
import llvm.type.ArrayType;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;
import llvm.type.PointerType;


public class GetElementPtrInstr extends Instr {
    public GetElementPtrInstr(String name, Value pointer, LLVMType elementType , Value offset) {
        super(new PointerType(elementType), name, InstrType.GETELEMENTPTR);
        addOperand(pointer);
        addOperand(offset);
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
}
