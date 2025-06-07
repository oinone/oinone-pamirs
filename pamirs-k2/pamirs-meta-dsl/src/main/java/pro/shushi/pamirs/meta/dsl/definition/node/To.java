package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("to")
public class To {

    @XStreamAsAttribute
    private String exp;

    @XStreamAsAttribute
    private String id;

    @XStreamOmitField
    private boolean sys;

    public To(String id) {
        this.id = id;
    }

    public To(String exp, String id) {
        this.exp = exp;
        this.id = id;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSys() {
        return sys;
    }

    public void setSys(boolean sys) {
        this.sys = sys;
    }

    @Override
    public String toString() {
        return "Transition [exp=" + exp + ", id=" + id + "]";
    }

}
