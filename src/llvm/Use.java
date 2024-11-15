package llvm;

public class Use {
    private final User user;
    private Value value;

    public Use(User user, Value value) {
        this.user = user;
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public Value getValue() {
        return value;
    }

    public void replace(Value newValue) {
        this.value = newValue;
    }
}
