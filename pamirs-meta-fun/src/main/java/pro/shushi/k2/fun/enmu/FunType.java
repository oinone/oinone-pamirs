package pro.shushi.k2.fun.enmu;

public enum FunType {

    LOCAL("LOCAL"),
    REMOTE("REMOTE"),
    ACTION("ACTION"),
    EXTPOINT("EXTPOINT"),
    FUNCTION("FUNCTION");

    private String type;

    public String getType() {
        return type;
    }

    FunType(String value) {
        this.type = value;
    }

}
