package pro.shushi.pamirs.meta.dsl.model;

public class Path {

    private String exp;
    private String to;

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void when(String exp) {
        this.exp = exp;
    }
}
