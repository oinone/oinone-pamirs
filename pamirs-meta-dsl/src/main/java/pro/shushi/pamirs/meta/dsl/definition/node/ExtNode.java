package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * 扩展点节点
 */
@XStreamAlias("extNode")
public class ExtNode extends Code {

	public ExtNode(String id) {
		super();
		this.setId(id);
	}

	@XStreamAsAttribute
	private String name;

	@XStreamAsAttribute
	private String namespace;

	@XStreamImplicit
	private List<Arg> args;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public List<Arg> getArgs() {
		return args;
	}

	public void setArgs(List<Arg> args) {
		this.args = args;
	}

	@Override
	public String toString() {
		return "ExtNode [id=" + this.getId() + ", description=" + this.getDesc() + ", name=" + name + "]";
	}
	
}
