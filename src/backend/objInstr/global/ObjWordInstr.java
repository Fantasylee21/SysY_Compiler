package backend.objInstr.global;

import backend.MipsBuilder;
import backend.objInstr.ObjInstr;

import java.util.ArrayList;

public class ObjWordInstr extends ObjInstr {
    private final String name;
    private final ArrayList<Integer> values;

    public ObjWordInstr(String name) {
        super();
        this.name = name;
        this.values = new ArrayList<>();
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    public ObjWordInstr(String name, Integer value) {
        super();
        this.name = name;
        this.values = new ArrayList<>();
        this.values.add(value);
        MipsBuilder.getMipsBuilder().addDataInstr(this);
    }

    public ObjWordInstr(String name, ArrayList<Integer> values) {
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
        if (allZero() && values.size() > 1) {
            return name + ": .word " + 0 + ":" + values.size();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": .word ");
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i != values.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
