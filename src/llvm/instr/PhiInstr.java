package llvm.instr;

public class PhiInstr extends Instr {
    public PhiInstr(String name) {
        super(null, name, InstrType.PHI);
    }
}
