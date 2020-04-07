package pro.shushi.pamirs.meta.dsl.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.shushi.pamirs.meta.dsl.Machine;
import pro.shushi.pamirs.meta.dsl.enumeration.EventExecutableStatus;
import pro.shushi.pamirs.meta.dsl.exception.DuplicateStateException;
import pro.shushi.pamirs.meta.dsl.exception.MachineException;
import pro.shushi.pamirs.meta.dsl.exception.MissTriggersException;
import pro.shushi.pamirs.meta.dsl.model.Event;
import pro.shushi.pamirs.meta.dsl.model.Process;
import pro.shushi.pamirs.meta.dsl.model.Place;
import pro.shushi.pamirs.meta.dsl.utils.ListUtils;

public class EventProcess {

	final static Logger logger = LoggerFactory.getLogger(EventProcess.class);

	public static EventProcessContext fireEvent(EventProcessContext eventContext) {

		Process process = Place.getProcessWithVersion(eventContext.processName, eventContext.processVersion);
		Machine machine = new Machine(process, eventContext.context);

		Event e = (Event) process.getStates().get(eventContext.executeEvent);
		// can't get event by name
		if (e == null) {
			logger.error("event not found on this stream: " + eventContext.executeEvent);
			throw new MachineException("event not found on this stream. process: " + process.getName() + ", event: "
					+ eventContext.executeEvent + ", executedPath: " + eventContext.executedPath);
		}
		
		EventExecutableStatus eventExecutableStatus = isEventExecutable(eventContext);
		if(EventExecutableStatus.MISSSTATE.equals(eventExecutableStatus)) {
			logger.warn("miss state so event cannot be fired: " + eventContext.executeEvent);
			throw new MissTriggersException("event cannot be fired. process: " + process.getName() + ", event: "
					+ eventContext.executeEvent + ", executedPath: " + eventContext.executedPath);
		} else if(EventExecutableStatus.DUPLICATESTATE.equals(eventExecutableStatus)) {
			logger.warn("duplicate state so event cannot be fired: " + eventContext.executeEvent);
			throw new DuplicateStateException("event not found on this stream. process: " + process.getName() + ", event: "
					+ eventContext.executeEvent + ", executedPath: " + eventContext.executedPath);
		}
		eventContext.context = machine.exec(eventContext.executeEvent);
		if (eventContext.processVersion == null) {
			eventContext.processVersion = process.getVersion();
		}
		return eventContext;
	}

	public static EventExecutableStatus isEventExecutable(EventProcessContext eventContext) {

		Process process = Place.getProcessWithVersion(eventContext.processName, eventContext.processVersion);

		Event e = (Event) process.getStates().get(eventContext.executeEvent);
		// if path and triggers all not null . then exec it
		if (ListUtils.isEmpty(e.getTriggers())) {
			return EventExecutableStatus.NORMAL;
		}
		// contains trigger but path is empty, then exit
		if(ListUtils.isEmpty(eventContext.executedPath)) {
			return EventExecutableStatus.MISSSTATE;
		}
		// single node and has been executed
		if(e.isSingle() && eventContext.executedPath.contains(e.getName())) {
			return EventExecutableStatus.DUPLICATESTATE;
		}
		for (String trigger : e.getTriggers()) {
			// contains trigger then fire
			if (eventContext.executedPath.contains(trigger.trim())) {
				return EventExecutableStatus.NORMAL;
			}
		}
		return EventExecutableStatus.MISSSTATE;
	}

}
