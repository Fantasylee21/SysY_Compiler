package llvm;

import llvm.type.ArrayType;
import llvm.type.Int8Type;
import llvm.type.LLVMType;
import llvm.type.PointerType;

public class PrintString extends GlobalValue {
    private String value;

    public PrintString(String name, String value) {
        super(new PointerType(new ArrayType(Int8Type.getInstance(), value.length() + 1)), name);
        this.value = value;
        LLVMBuilder.getLlvmBuilder().addPrintString(this);
    }

    @Override
    public String toString() {
        String content = value.replace("\\", "\\5C").replace("\n", "\\0A").replace("\0", "\\00");
        return name + " = private unnamed_addr constant [" + (value.length() + 1) + " x i8] c\"" + content + "\\00\"";
    }

}
