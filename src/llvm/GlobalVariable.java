package llvm;

import backend.objInstr.global.ObjByteInstr;
import backend.objInstr.global.ObjWordInstr;
import llvm.initial.ArrayInitial;
import llvm.initial.Initial;
import llvm.initial.VarInitial;
import llvm.type.ArrayType;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;

public class GlobalVariable extends  GlobalValue {
    private Initial initial;

    public GlobalVariable(LLVMType type, String name, Initial initial) {
        super(type, "@" + name);
        this.initial = initial;
        this.initial.setName(name);
        LLVMBuilder.getLlvmBuilder().addGlobalVariable(this);
    }

    public Initial getInitial() {
        return initial;
    }

    @Override
    public String toString() {
        return name + " = dso_local global " + initial.toString();
    }

    @Override
    public void generateMips() {
        if (initial.getType().getType() == LLVMEnumType.Int8Type) {
            VarInitial varInitial = (VarInitial) initial;
//            new ObjByteInstr(name.substring(1), varInitial.getValue());
            new ObjWordInstr(name.substring(1), varInitial.getValue());
        } else if (initial.getType().getType() == LLVMEnumType.Int32Type) {
            VarInitial varInitial = (VarInitial) initial;
            new ObjWordInstr(name.substring(1), varInitial.getValue());
        } else {
            ArrayInitial arrayInitial = (ArrayInitial) initial;
            ArrayType arrayType = (ArrayType) initial.getType();
            LLVMType elementType = arrayType.getElementType();
            if (elementType.getType() == LLVMEnumType.Int8Type) {
//                new ObjByteInstr(name.substring(1), arrayInitial.getValues());
                new ObjWordInstr(name.substring(1), arrayInitial.getValues());
            } else {
                new ObjWordInstr(name.substring(1), arrayInitial.getValues());
            }
        }
    }
}
