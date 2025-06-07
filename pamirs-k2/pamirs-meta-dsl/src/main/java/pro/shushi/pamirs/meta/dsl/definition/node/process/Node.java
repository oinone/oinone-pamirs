package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.List;

public class Node {

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String description;

    @XStreamAlias("transitions")
    private List<Transition> transitions;

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

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    @Override
    public String toString() {
        return "Node [name=" + name + ", description=" + description + ", transitions=" + transitions + "]";
    }

}
