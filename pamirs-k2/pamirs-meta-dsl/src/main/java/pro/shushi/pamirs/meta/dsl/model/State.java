package pro.shushi.pamirs.meta.dsl.model;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;
import pro.shushi.pamirs.meta.dsl.definition.node.To;
import pro.shushi.pamirs.meta.dsl.exception.MachineException;
import pro.shushi.pamirs.meta.dsl.extend.ResultFuture;
import pro.shushi.pamirs.meta.dsl.signal.Exe;
import pro.shushi.pamirs.meta.dsl.signal.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Slf4j
public class State {

    private String name;
    private List<Path> nexts;

    // signal point
    private List<Exe> exes;

    private To ex;

    public State(String name) {
        this.name = name;
        this.nexts = new ArrayList<>();
        this.exes = new ArrayList<>();
    }

    public Path to(State state) {
        Path path = new Path();
        path.setTo(state.name);
        nexts.add(path);
        return path;
    }

    public void execute(Map<String, Object> context) {
        log.error("开始执行节点:{}", this.getName());
        for (Exe exe : this.exes) {
            log.error("节点执行逻辑:{}", exe.getClass().getSimpleName());
            exe.dispatch(context);
        }

        afterExecute(context);
        log.error("节点执行结束:{} \r\n", this.getName());
    }

    private void afterExecute(Map<String, Object> context) {
        Map<Slot, Object> successResults = (Map<Slot, Object>) context.get(DSLDefineConstants.SUCCESS_SLOTS_RESULT_MAP);
        boolean hasFail = Boolean.valueOf(String.valueOf(context.get(DSLDefineConstants.SOME_SLOTS_EXECUTE_FAIL))).booleanValue();
        if (null == successResults && !hasFail) {
            return;
        }

        boolean allSuccess = true;
        if (null != successResults) {
            Set<Entry<Slot, Object>> set = successResults.entrySet();
            for (Entry<Slot, Object> entry : set) {
                try {
                    Object obj = entry.getValue();
                    if (entry.getValue() instanceof ResultFuture) {
                        Object result = ((ResultFuture) obj).get();

                        // insert result to context
                        if (StringUtils.isNotBlank(entry.getKey().getResult())) {
                            context.put(entry.getKey().getResult(), result);
                        }
                    }
                } catch (Exception e) {
                    set.remove(entry);
                    allSuccess = false;
                }
            }
        }

        allSuccess &= !hasFail;
        if (!allSuccess) {
            throw new MachineException("当前节点为[" + name + "],执行失败");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Path> getNexts() {
        return nexts;
    }

    public void setNexts(List<Path> nexts) {
        this.nexts = nexts;
    }

    public List<Exe> getExes() {
        return exes;
    }

    public void setExes(List<Exe> exes) {
        this.exes = exes;
    }

    public To getEx() {
        return ex;
    }

    public void setEx(To ex) {
        this.ex = ex;
    }
}
