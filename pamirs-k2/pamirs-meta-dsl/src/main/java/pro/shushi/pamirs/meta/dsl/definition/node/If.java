package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("if")
public class If extends Code {

    public If(String id) {
        super();
        this.setId(id);
    }

    @XStreamAsAttribute
    private String exp;

    @XStreamAlias("else")
    private To els;

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public To getEls() {
        return els;
    }

    public void setEls(To els) {
        this.els = els;
    }

    @Override
    public String toString() {
        return "If [id=" + this.getId() + ", expression=" + exp + ", tos=" + getTos() + ", else=" + els + "]";
    }

}
