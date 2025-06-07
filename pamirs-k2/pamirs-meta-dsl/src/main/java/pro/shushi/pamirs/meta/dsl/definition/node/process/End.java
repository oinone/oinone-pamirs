package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("end")
public class End {

    @XStreamAsAttribute
    private String name;

    public End(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "End [name=" + name + "]";
    }

}
