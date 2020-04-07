package pro.shushi.pamirs.meta.dsl.definition.node.process;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("transition")
public class Transition {

	@XStreamAsAttribute
	private String exp;

	@XStreamAsAttribute
	private String to;

	public Transition(String to) {
		this.to = to;
	}

	public Transition(String exp, String to) {
		this.exp = exp;
		this.to = to;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "Transition [exp=" + exp + ", to=" + to + "]";
	}

}
