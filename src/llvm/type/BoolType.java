package llvm.type;

public class BoolType extends LLVMType {
    private static BoolType instance = new BoolType();

    private BoolType() {
        bitWidth = 1;
    }

    public static BoolType getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "i1";
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.BoolType;
    }
}
