package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("start")
public class Start {

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String description;

    @XStreamAlias("goto")
    private Transition transition;

    public Start(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    @Override
    public String toString() {
        return "Start [name=" + name + ", description=" + description + ", transition=" + transition + "]";
    }

}
