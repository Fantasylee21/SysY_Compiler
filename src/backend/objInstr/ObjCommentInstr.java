package backend.objInstr;

import backend.MipsBuilder;

public class ObjCommentInstr extends ObjInstr {
    private String comment;

    public ObjCommentInstr(String comment) {
        this.comment = comment;
        MipsBuilder.getMipsBuilder().addTextInstr(this);
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "\n# " + comment;
    }
}
