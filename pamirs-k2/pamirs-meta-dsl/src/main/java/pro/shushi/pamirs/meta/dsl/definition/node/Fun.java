package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("fun")
public class Fun extends Code {

    public Fun(String id) {
        super();
        this.setId(id);
    }

    @XStreamAsAttribute
    private String namespace;

    @XStreamAsAttribute
    private String name;

    @XStreamImplicit
    private List<Arg> args;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Fun [id=" + this.getId() + ", description=" + this.getDesc() + ", namespace=" + namespace + ", name=" + name + ", args=" + args + "]";
    }

}
