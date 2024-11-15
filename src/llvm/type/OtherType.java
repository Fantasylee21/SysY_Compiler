package llvm.type;

public class OtherType extends LLVMType {
    private static final OtherType BasicBlock = new OtherType();
    private static final OtherType Module = new OtherType();
    private static final OtherType Label = new OtherType();
    private static final OtherType Function = new OtherType();

    public static OtherType getBasicBlock() {
        return BasicBlock;
    }

    public static OtherType getModule() {
        return Module;
    }

    public static OtherType getLabel() {
        return Label;
    }

    public static OtherType getFunction() {
        return Function;
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.OtherType;
    }
}
