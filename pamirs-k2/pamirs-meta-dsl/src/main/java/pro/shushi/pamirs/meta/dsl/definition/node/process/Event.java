package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("event")
public class Event extends Node {

    @XStreamAsAttribute
    public String rules;
    @XStreamAsAttribute
    public String triggers;
    @XStreamAsAttribute
    public String single;
    @XStreamImplicit
    public List<Invocable> invocables;

    public Event(String name) {
        this.setName(name);
    }

    @Override
    public String toString() {
        return "Event [rules=" + rules + ", triggers=" + triggers + ", invocables=" + invocables + ", name=" + getName()
                + ", transitions=" + getTransitions() + "]";
    }

}
