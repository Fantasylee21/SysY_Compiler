package llvm;

import llvm.type.Int32Type;

public class Constant extends Value {
    private final int value;

    public Constant(int value) {
        super(Int32Type.getInstance(), Integer.toString(value));
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getType().toString() + " " + value;
    }

}
