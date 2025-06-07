package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("new")
public class New extends Code {

    public New(String id) {
        super();
        this.setId(id);
    }

    @XStreamAsAttribute
    private String model;

    @XStreamImplicit(itemFieldName = "field")
    private List<Arg> fields;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Arg> getFields() {
        return fields;
    }

    public void setFields(List<Arg> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "New [id=" + this.getId() + ", description=" + this.getDesc() + ", model=" + model + ", fields=" + fields + "]";
    }

}
