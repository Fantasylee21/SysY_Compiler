package llvm.initial;

import llvm.type.ArrayType;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;

import java.util.ArrayList;

public class ArrayInitial extends Initial {
    private ArrayList<Integer> values;

    public ArrayInitial(LLVMType type, String name, ArrayList<Integer> values) {
        super(type, name);
        this.values = values;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public void init(int size) {
        while (values.size() < size) {
            values.add(0);
        }
    }

    public boolean isAllZero() {
        for (Integer value : values) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (values.isEmpty() || isAllZero()) {
            return type.toString() + " zeroinitializer";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(type.toString()).append(" ");
            ArrayType arrayType = (ArrayType) type;
            if (arrayType.getElementType().getType() == LLVMEnumType.Int8Type) {
                sb.append("c").append("\"");
                for (Integer value : values) {
                    if (value == 0) {
                        sb.append("\\00");
                        continue;
                    } else if (value == 10) {
                        sb.append("\\0A");
                        continue;
                    } else if (value == 13) {
                        sb.append("\\0D");
                        continue;
                    } else if (value == 92) {
                        sb.append("\\5C");
                        continue;
                    } else if (value == 34) {
                        sb.append("\\22");
                        continue;
                    }
                    sb.append((char)value.intValue());
                }
                sb.append("\"").append(", align 1");
            } else {
                sb.append("[");
                for (int i = 0; i < values.size(); i++) {
                    sb.append(((ArrayType) type).getElementType().toString()).append(" ").append(values.get(i).toString());
                    if (i != values.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
            }
            return sb.toString();
        }
    }
}
