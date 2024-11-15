package llvm.type;

public class VoidType extends LLVMType {
    private static final VoidType instance = new VoidType();

    public static VoidType getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.VoidType;
    }
}
