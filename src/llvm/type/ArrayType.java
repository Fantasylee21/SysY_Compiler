package llvm.type;

public class ArrayType extends LLVMType {
    private final LLVMType elementType;
    private final int size;

    public ArrayType(LLVMType elementType, int size) {
        this.elementType = elementType;
        this.size = size;
    }

    public LLVMType getElementType() {
        return elementType;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "[" + size + " x " + elementType + "]";
    }

    @Override
    public LLVMEnumType getType() {
        return LLVMEnumType.ArrayType;
    }
}
