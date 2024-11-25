package backend.objInstr.global;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;

import java.util.ArrayList;

public class ObjByteInstr extends ObjInstr{
    private final String name;
    private final ArrayList<Integer> values;

    // TODO: 因为全局是1字节，对代码生成没有影响，所以这里直接用ObjWordInstr

    public ObjByteInstr(String name) {
        super();
        this.name = name;
        this.values = new ArrayList<>();
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    public ObjByteInstr(String name, Integer value) {
        super();
        this.name = name;
        this.values = new ArrayList<>();
        this.values.add(value);
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    public ObjByteInstr(String name, ArrayList<Integer> values) {
        super();
        this.name = name;
        this.values = values;
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    public void addValue(Integer value) {
        values.add(value);
    }

    public boolean allZero() {
        for (int value : values) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (allZero()) {
            return name + ": .byte " + 0 + ":" + values.size();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": .byte ");
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i != values.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
