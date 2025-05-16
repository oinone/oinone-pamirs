package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("service")
public class Service extends Node {

	@XStreamImplicit
	public List<Invocable> invocables;

	public Service(String name) {
		this.setName(name);
	}

	@Override
	public String toString() {
		return "Service [invocables=" + invocables + ", name=" + getName()
				+ ", transitions=" + this.getTransitions() + "]";
	}

}
