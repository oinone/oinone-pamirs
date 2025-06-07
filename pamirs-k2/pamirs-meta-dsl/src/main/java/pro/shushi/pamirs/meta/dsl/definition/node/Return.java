package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("return")
public class Return extends Code {

    public Return(String id) {
        super();
        this.setId(id);
    }

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String exp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "End [id=" + this.getId() + "]";
    }

}
