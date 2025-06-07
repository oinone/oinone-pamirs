package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("delete")
public class Delete extends Code {

    public Delete(String id) {
        super();
        this.setId(id);
    }

    @XStreamAsAttribute
    private String model;

    @XStreamAsAttribute
    private String arg;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "Delete [id=" + this.getId() + ", description=" + this.getDesc() + ", model=" + model + ", arg=" + arg + "]";
    }

}
