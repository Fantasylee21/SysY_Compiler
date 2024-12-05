package backend;

import backend.objInstr.*;
import backend.objInstr.branch.ObjBranchInstr;
import backend.objInstr.dm.ObjDmInstr;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.move.MoveType;
import backend.objInstr.move.ObjMoveHLInstr;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.rrCalculate.ObjRRCalculateInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.register.Register;

import java.util.ArrayList;
import java.util.HashSet;

public class ObjBlock {
    private ArrayList<ObjInstr> instructions;
    private String name;
    private ArrayList<ObjBlock> successors;
    private ArrayList<ObjBlock> predecessors;

    public ObjBlock(String name) {
        this.name = name;
        this.instructions = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
    }

    public void addInstr(ObjInstr instr) {
        instructions.add(instr);
    }

    public ArrayList<ObjInstr> getInstructions() {
        return instructions;
    }

    public String getName() {
        return name;
    }

    public void setSuccessors(ArrayList<ObjBlock> successors) {
        this.successors = successors;
    }

    public void setPredecessors(ArrayList<ObjBlock> predecessors) {
        this.predecessors = predecessors;
    }

    public ArrayList<ObjBlock> getSuccessors() {
        return successors;
    }

    public ArrayList<ObjBlock> getPredecessors() {
        return predecessors;
    }


    public String toString(int curOffset) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n\t");
        for (ObjInstr instr : instructions) {
            if (instr instanceof ObjJRInstr) {
                sb.append("addu $sp, $sp, ").append(curOffset).append("\n\t");
            }
            sb.append(instr.toString()).append("\n\t");
        }
        return sb.toString();
    }

    private HashSet<String> in = new HashSet<>();
    private HashSet<String> out = new HashSet<>();
    private HashSet<String> use = new HashSet<>();
    private HashSet<String> def = new HashSet<>();

    public void setIn(HashSet<String> in) {
        this.in = in;
    }

    public void setOut(HashSet<String> out) {
        this.out = out;
    }

    public void setUse(HashSet<String> use) {
        this.use = use;
    }

    public void setDef(HashSet<String> def) {
        this.def = def;
    }

    public HashSet<String> getIn() {
        return in;
    }

    public HashSet<String> getOut() {
        return out;
    }

    public HashSet<String> getUse() {
        return use;
    }

    public HashSet<String> getDef() {
        return def;
    }

    public void buildUseDef() {
        def = new HashSet<>();
        use = new HashSet<>();
        for (ObjInstr instr : instructions) {
            if (instr instanceof ObjBranchInstr) {
                Register rs = ((ObjBranchInstr) instr).getRs();
                Register rt = ((ObjBranchInstr) instr).getRt();
                if (!rs.isRealRegister()) {
                    use.add(rs.getVirtualReg());
                }
                if (!rt.isRealRegister()) {
                    use.add(rt.getVirtualReg());
                }
            } else if (instr instanceof ObjDmInstr) {
                Register rs = ((ObjDmInstr) instr).getRs();
                Register rt = ((ObjDmInstr) instr).getRt();
                if (!rs.isRealRegister()) {
                    use.add(rs.getVirtualReg());
                }
                if (!rt.isRealRegister()) {
                    use.add(rt.getVirtualReg());
                }
            } else if (instr instanceof ObjLoadInstr) {
                Register base = ((ObjLoadInstr) instr).getBase();
                Register rt = ((ObjLoadInstr) instr).getRt();
                if (!base.isRealRegister()) {
                    use.add(base.getVirtualReg());
                }
                if (!rt.isRealRegister()) {
                    def.add(rt.getVirtualReg());
                }
            } else if (instr instanceof ObjMoveHLInstr) {
                Register rd = ((ObjMoveHLInstr) instr).getRd();
                MoveType moveType = ((ObjMoveHLInstr) instr).getMoveType();
                if (!rd.isRealRegister()) {
                    if (moveType == MoveType.MFLO || moveType == MoveType.MFHI) {
                        def.add(rd.getVirtualReg());
                    } else {
                        use.add(rd.getVirtualReg());
                    }
                }
            } else if (instr instanceof ObjRICalculate) {
                Register rs = ((ObjRICalculate) instr).getRs();
                Register rt = ((ObjRICalculate) instr).getRt();
                if (!rs.isRealRegister()) {
                    use.add(rs.getVirtualReg());
                }
                if (!rt.isRealRegister()) {
                    def.add(rt.getVirtualReg());
                }
            } else if (instr instanceof ObjRRCalculateInstr) {
                Register rs = ((ObjRRCalculateInstr) instr).getRs();
                Register rt = ((ObjRRCalculateInstr) instr).getRt();
                Register rd = ((ObjRRCalculateInstr) instr).getRd();
                if (!rs.isRealRegister()) {
                    use.add(rs.getVirtualReg());
                }
                if (!rt.isRealRegister()) {
                    use.add(rt.getVirtualReg());
                }
                if (!rd.isRealRegister()) {
                    def.add(rd.getVirtualReg());
                }
            } else if (instr instanceof ObjMoveInstr) {
                Register src = ((ObjMoveInstr) instr).getSrc();
                Register dest = ((ObjMoveInstr) instr).getDst();
                if (!src.isRealRegister()) {
                    use.add(src.getVirtualReg());
                }
                if (!dest.isRealRegister()) {
                    def.add(dest.getVirtualReg());
                }
            } else if (instr instanceof ObjStoreInstr) {
                Register rt = ((ObjStoreInstr) instr).getRt();
                Register base = ((ObjStoreInstr) instr).getBase();
                if (!rt.isRealRegister()) {
                    use.add(rt.getVirtualReg());
                }
                if (!base.isRealRegister()) {
                    use.add(base.getVirtualReg());
                }
            } else if (instr instanceof ObjLaInstr) {
                Register target = ((ObjLaInstr) instr).getTarget();
                if (!target.isRealRegister()) {
                    def.add(target.getVirtualReg());
                }
            } else if (instr instanceof ObjLiInstr) {
                Register target = ((ObjLiInstr) instr).getTarget();
                if (!target.isRealRegister()) {
                    def.add(target.getVirtualReg());
                }
            }
        }
    }
}
