package pro.shushi.pamirs.meta.dsl.model;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Process {

	private String				name;

	private int					version;

	private String				first;

	private Map<String, State>	states;

	public Process(String name, int version) {
		this.name = name;
		this.version = version;
		states = new HashMap<>();
		
		if(StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("name is blank");
		}
	}

	public State createState(String name) {
		State state = new State(name);
		states.put(name, state);
		return state;
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

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public Map<String, State> getStates() {
		return states;
	}

	public void setStates(Map<String, State> states) {
		this.states = states;
	}
}
