package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("action")
public class Action extends Code {

	public Action(String id) {
		super();
		this.setId(id);
	}

	@XStreamAsAttribute
	private String model;

	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String arg;

	@Override
	public String toString() {
		return "Action [id=" + this.getId() + ", description=" + this.getDesc() + ", model=" + model + ", name=" + name + ", arg=" + arg + "]";
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}
}
