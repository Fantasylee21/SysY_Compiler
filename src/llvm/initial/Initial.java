package llvm.initial;

import llvm.type.ArrayType;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;

import java.util.ArrayList;

public class Initial {
    protected LLVMType type;
    protected String name;

    public Initial(LLVMType type, String name) {
        this.type = type;
        this.name = name;
    }

    public LLVMType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
