package llvm.type;

public class Int8Type extends LLVMType {
    private static Int8Type instance = new Int8Type();

    private Int8Type() {
        bitWidth = 8;
    }

    public static Int8Type getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "i8";
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.Int8Type;
    }
}
