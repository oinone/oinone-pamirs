package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("assign")
public class Assign extends Code {

	public Assign(String id) {
		super();
		this.setId(id);
	}

	@XStreamAsAttribute
	private String arg;

	@XStreamImplicit(itemFieldName = "field")
	private List<Arg> fields;

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	public List<Arg> getFields() {
		return fields;
	}

	public void setFields(List<Arg> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "Assign [id=" + this.getId() + ", description=" + this.getDesc() + ", arg=" + arg + ", fields=" + fields + "]";
	}
	
}
