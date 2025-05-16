package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("logic")
public class Logic {

	@XStreamAsAttribute
	private String		name;

	@XStreamAsAttribute
	private String		desc;

	@XStreamAsAttribute
	private int			version;

	private Begin start;

	@XStreamImplicit
	private List<Code>	nodes;

	@XStreamImplicit(itemFieldName="return")
	private List<Return>	ends;

	public Logic(String name, int version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Begin getStart() {
		return start;
	}

	public void setStart(Begin start) {
		this.start = start;
	}

	public List<Code> getNodes() {
		return nodes;
	}

	public void setNodes(List<Code> nodes) {
		this.nodes = nodes;
	}

	public List<Return> getEnds() {
		return ends;
	}

	public void setEnds(List<Return> ends) {
		this.ends = ends;
	}

	@Override
	public String toString() {
		return "Logic [name=" + name + ", version=" + version + ", description=" + desc + ", start=" + start + ", nodes=" + nodes
				+  ", ends=" + ends + "]";
	}

}
