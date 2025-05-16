package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("process")
public class Definition {

	@XStreamAsAttribute
	private String		name;

	@XStreamAsAttribute
	private int			version;

	private Start start;

	@XStreamImplicit
	private List<Node>	nodes;

	@XStreamImplicit
	private List<End>	ends;

	public Definition(String name, int version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Start getStart() {
		return start;
	}

	public void setStart(Start start) {
		this.start = start;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<End> getEnds() {
		return ends;
	}

	public void setEnds(List<End> ends) {
		this.ends = ends;
	}

	@Override
	public String toString() {
		return "Definition [name=" + name + ", version=" + version + ", start=" + start + ", nodes=" + nodes
				+  ", ends=" + ends + "]";
	}

}
