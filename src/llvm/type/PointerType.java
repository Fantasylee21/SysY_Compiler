package llvm.type;

public class PointerType extends LLVMType {
    private final LLVMType targetType;

    public PointerType(LLVMType targetType) {
        this.targetType = targetType;
        this.bitWidth = 64;
    }

    public LLVMType getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return targetType.toString() + "*";
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.PointerType;
    }
}
