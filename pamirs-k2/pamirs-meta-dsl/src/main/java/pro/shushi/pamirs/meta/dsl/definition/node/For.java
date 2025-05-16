package pro.shushi.pamirs.meta.dsl.definition.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("for")
public class For extends Code {

	public For(String id) {
		super();
		this.setId(id);
	}

	@XStreamAsAttribute
	private String start;

	@XStreamAsAttribute
	private String end;

	@XStreamAsAttribute
	private String step;

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	@Override
	public String toString() {
		return "foreach [id=" + this.getId() + ", start=" + start + ", end=" + end + ", step=" + step + "]";
	}
	
}
