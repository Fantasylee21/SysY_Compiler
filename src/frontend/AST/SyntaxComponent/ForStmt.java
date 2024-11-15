package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Error.SymbolErrors;
import llvm.LLVMBuilder;
import llvm.Value;
import llvm.instr.StoreInstr;
import llvm.instr.TruncInstr;
import llvm.instr.ZextInstr;
import llvm.type.*;

import java.lang.annotation.Target;
import java.util.ArrayList;
// ForStmt ==> LVal '=' Exp
public class ForStmt extends Node {
    public ForStmt(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    @Override
    public void checkErrors() {
        LVal lVal = (LVal) children.get(0);
        if (lVal.isConst()) {
            SymbolErrors.getInstance().addError(lVal.getStartLine(), "h");
        }
        super.checkErrors();
    }

    @Override
    public Value generateIR() {
        LVal lVal = (LVal) children.get(0);
        Value LValValue = lVal.generateIRForLVal(true);
        Value expValue = children.get(2).generateIR();
        LLVMType targetType = ((PointerType) LValValue.getType()).getTargetType();
        if (targetType.getType() == LLVMEnumType.Int8Type && expValue.getType().getType() == LLVMEnumType.Int32Type) {
            expValue = new TruncInstr(LLVMBuilder.getLlvmBuilder().getVarName(), expValue, Int8Type.getInstance());
        } else if (targetType.getType() == LLVMEnumType.Int32Type && expValue.getType().getType() == LLVMEnumType.Int8Type) {
            expValue = new ZextInstr(LLVMBuilder.getLlvmBuilder().getVarName(), expValue, Int32Type.getInstance());
        }
        return new StoreInstr(null, expValue, LValValue);
    }
}
