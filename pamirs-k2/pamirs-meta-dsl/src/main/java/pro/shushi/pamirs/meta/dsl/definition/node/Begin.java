package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("start")
public class Begin {

    @XStreamAsAttribute
    private String id;

    @XStreamAsAttribute
    private String desc;

    @XStreamAsAttribute
    private String model;

    @XStreamAlias("to")
    private To to;

    public Begin(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public To getTo() {
        return to;
    }

    public void setTo(To to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Start [model = " + model + "id = " + id + ",description = " + desc + ", to = " + to + "]";
    }

}
