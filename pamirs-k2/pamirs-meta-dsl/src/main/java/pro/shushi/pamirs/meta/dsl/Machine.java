package pro.shushi.pamirs.meta.dsl;

import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;
import pro.shushi.pamirs.meta.dsl.exception.MachineException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;
import pro.shushi.pamirs.meta.dsl.model.Path;
import pro.shushi.pamirs.meta.dsl.model.Process;
import pro.shushi.pamirs.meta.dsl.model.State;

import java.util.Map;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.BASE_MACHINE_ERROR;

public class Machine {

	final Logger				logger	= LoggerFactory.getLogger(Machine.class);

	private Process process;

	private Map<String, Object>	context;

	private boolean isLogic;

	public Machine(Process process, Map<String, Object> context) {
		this.process = process;
		this.context = context;
	}

	public Machine(Process process, Map<String, Object> context, boolean isLogic) {
		this.process = process;
		this.context = context;
		this.isLogic = isLogic;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public boolean isLogic() {
		return isLogic;
	}

	public void setLogic(boolean logic) {
		isLogic = logic;
	}

	/**
	 * execute a state of the machine.
	 *
	 * @param stateName
	 * @return
	 */
	public Map<String, Object> exec(String stateName) {
		context.put(DSLDefineConstants.CURRENT_STATE_NAME, stateName);
		State s = process.getStates().get(stateName);
		if (s != null) {
			s.execute(context);
			return context;
		}
		logger.error("state not found on this process: " + process.getName() + " state :" + stateName);
		throw new MachineException("state not found on this process: "+ process.getName() + " state:" + stateName);
	}

	/**
	 * find next state
	 *
	 * @param s
	 * @return
	 */
	public String next(State s) {
		String next = null;
		for (Path path : s.getNexts()) {
			// has "exp" and "to" #=> eval exp then if true to = next else
			// continue
			// has "to" and "exp" == null #=> then to = next
//			logger.debug("compute next state,"+"exp:"+path.getExp()+",to:"+path.getTo()+",context:"+ JsonUtils.toJSONString(context));
			if (path.getExp() == null)
				next = path.getTo();
			else {
				Boolean result;
				if(isLogic){
					try{
						result = (Boolean) LogicFunInvoker.exp(path.getExp(), context);
					}catch (Exception e){
						throw PamirsException.construct(BASE_MACHINE_ERROR, e).errThrow();
					}
				}else{
					result = MVEL.evalToBoolean(path.getExp(), context);
				}
				logger.debug("compute next state,result:"+result);
				if (result != null && result){
					next = path.getTo();
					break;
				}else{
					continue;
				}
			}
		}
		if (!s.getNexts().isEmpty() && next == null) {
			if(!isLogic){
				logger.error("next state not found on state: " + s.getName());
				throw new MachineException("state not found on process: " + process.getName() + " state: " + s.getName());
			}
		}
		return next;
	}

}
