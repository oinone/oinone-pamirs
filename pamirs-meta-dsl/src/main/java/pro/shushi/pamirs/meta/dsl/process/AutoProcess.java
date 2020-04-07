package pro.shushi.pamirs.meta.dsl.process;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.dsl.Machine;
import pro.shushi.pamirs.meta.dsl.definition.node.To;
import pro.shushi.pamirs.meta.dsl.model.Place;
import pro.shushi.pamirs.meta.dsl.model.Process;
import pro.shushi.pamirs.meta.dsl.model.State;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.Map;

public class AutoProcess {
	final static Logger	logger	= LoggerFactory.getLogger(AutoProcess.class);

	public static Map<String, Object> run(String processName, Map<String, Object> context) {
		return run(processName, context, Boolean.FALSE);
	}

	public static Map<String, Object> run(String processName, Map<String, Object> context, boolean isLogic) {
		Process process = Place.getProcess(processName);
		Machine machine = new Machine(process, context, isLogic);

		String runState = process.getFirst();
		do {
			try{
                logger.debug("AutoProcess start running on " + process.getName() + "'s state " + runState);
				machine.exec(runState);
				logger.debug("AutoProcess running on " + process.getName() + "'s state " + runState);
			}catch (Throwable e){
				if(isLogic){
					State state = process.getStates().get(runState);
					To toException = state != null ? state.getEx() : null;
					if(null != toException && StringUtils.isNotBlank(toException.getId())){
						runState = toException.getId();
						Map<String, Object> emap = JsonUtils.parseObject(JsonUtils.toJSONString(e), new TypeReference<Map<String, Object>>() {});
						context.put(runState, emap.containsKey("code")?emap.get("code"):10000115);
                        logger.debug("AutoProcess throw an exception, Exception code:" + context.get(runState));
					}else{
                        logger.debug("AutoProcess throw an exception");
						throw e;
					}
				}else{
                    logger.debug("AutoProcess throw an exception");
					throw e;
				}
			}
			runState = machine.next(process.getStates().get(runState));
		} while (runState != null && !runState.trim().equals(""));

		return machine.getContext();

	}

}
