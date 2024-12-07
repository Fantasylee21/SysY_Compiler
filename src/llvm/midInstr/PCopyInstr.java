package llvm.midInstr;

import llvm.Value;

import java.util.ArrayList;

public class PCopyInstr extends MidInstr {
    private ArrayList<Value> dsts;
    private ArrayList<Value> srcs;

    public PCopyInstr() {
        super(null, null, MidInstrType.PCOPY, true);
        dsts = new ArrayList<>();
        srcs = new ArrayList<>();
    }

    public void addCopy(Value dst, Value src) {
        dsts.add(dst);
        srcs.add(src);
    }

    public ArrayList<Value> getDsts() {
        return dsts;
    }

    public ArrayList<Value> getSrcs() {
        return srcs;
    }


}
