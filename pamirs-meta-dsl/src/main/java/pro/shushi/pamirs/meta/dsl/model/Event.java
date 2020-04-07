package pro.shushi.pamirs.meta.dsl.model;

import java.util.List;


public class Event extends State{

	public Event(String name) {
		super(name);
	}

	private List<String> rules;

	private List<String> triggers;

	private boolean single;

	public List<String> getRules() {
		return rules;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	public List<String> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<String> triggers) {
		this.triggers = triggers;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}
}
