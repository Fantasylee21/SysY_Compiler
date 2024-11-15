package llvm;

public class Loop {
    private BasicBlock cond;
    private BasicBlock body;
    private BasicBlock update;
    private BasicBlock exit;

    public Loop(BasicBlock cond, BasicBlock body, BasicBlock update, BasicBlock exit) {
        this.cond = cond;
        this.body = body;
        this.update = update;
        this.exit = exit;
    }

    public BasicBlock getCond() {
        return cond;
    }

    public BasicBlock getBody() {
        return body;
    }

    public BasicBlock getUpdate() {
        return update;
    }

    public BasicBlock getExit() {
        return exit;
    }

    public void setCond(BasicBlock cond) {
        this.cond = cond;
    }

    public void setBody(BasicBlock body) {
        this.body = body;
    }

    public void setUpdate(BasicBlock update) {
        this.update = update;
    }

    public void setExit(BasicBlock exit) {
        this.exit = exit;
    }

}
