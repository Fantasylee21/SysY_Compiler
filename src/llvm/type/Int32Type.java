package llvm.type;

public class Int32Type extends LLVMType {
    private static final Int32Type instance = new Int32Type();

    private Int32Type() {
        bitWidth = 32;
    }

    public static Int32Type getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "i32";
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.Int32Type;
    }
}
