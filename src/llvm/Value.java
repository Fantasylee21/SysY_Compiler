package llvm;

import llvm.type.LLVMType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Value {
    protected LLVMType type;
    protected String name;
    protected ArrayList<Use> useList;

    public Value(LLVMType type, String name) {
        this.type = type;
        this.name = name;
        this.useList = new ArrayList<>();
    }

    public LLVMType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(LLVMType type) {
        this.type = type;
    }

    public ArrayList<Use> getUseList() {
        return useList;
    }

    public void addUse(User user) {
        useList.add(new Use(user, this));
    }

    public void removeUser(User user) {
        Iterator<Use> iterator = useList.iterator();
        while (iterator.hasNext()) {
            Use use = iterator.next();
            if (use.getUser() == user) {
                iterator.remove();
                break;
            }
        }
    }

    public void replaceAllUsesWith(Value newValue) {
        ArrayList<User> users = useList.stream()
                .map(Use::getUser)
                .collect(Collectors.toCollection(ArrayList::new));
        for (User user : users) {
            user.replaceValue(this, newValue);
        }

    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }

    public void generateMips() {
        return;
    }

}
