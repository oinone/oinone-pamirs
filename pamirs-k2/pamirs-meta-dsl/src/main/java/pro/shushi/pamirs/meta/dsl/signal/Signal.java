package pro.shushi.pamirs.meta.dsl.signal;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signal implements Exe {

    protected final Logger logger = LoggerFactory.getLogger(Signal.class);

    private List<Slot> slots;

    public Signal() {
        this.slots = new ArrayList<Slot>();
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public Signal add(Object listener, String method, String args, String result) {
        Slot slot = new Slot(listener, true, method, args, result);
        slots.add(slot);
        return this;
    }

    public Signal add(Object listener, Boolean sync, String method, String args, String result) {
        Slot slot = new Slot(listener, sync, method, args, result);
        slots.add(slot);
        return this;
    }

    @Override
    public void dispatch(Map<String, Object> context) {
        List<String> executedSlots = (List<String>) context.get(DSLDefineConstants.EXECUTED_SLOTS_DESCRIPTION);
        for (Slot slot : slots) {
            try {
                // check if need to ignore
                if (CollectionUtils.isNotEmpty(executedSlots) && executedSlots.contains(slot.toMD5Description())) {
                    continue;
                }

                // get args context key
                String[] params = slot.getArgs().split(",");
                // get paramsType and arguments
                Class<?>[] paramsType = new Class<?>[params.length];
                Object[] args = new Object[params.length];
                for (int i = 0; i < params.length; i++) {
                    args[i] = MVEL.eval(params[i], context);
                    paramsType[i] = args[i].getClass();
                }
                // get callback context key
                String callback = slot.getResult();
                // invoke listener (sync or not
                Object result = null;
                if (Boolean.TRUE.equals(slot.getSync())) {
                    result = this.invoke(slot, paramsType, args);
                } else {
                    PendingTask task = new PendingTask(slot, paramsType, args);
                    Executor.execute(task);
                }
                // insert result to context
                if (StringUtils.isNotBlank(callback)) {
                    context.put(callback, result);
                }

                // put result into return context
                Map<Slot, Object> successResults = (Map<Slot, Object>) context.get(DSLDefineConstants.SUCCESS_SLOTS_RESULT_MAP);
                if (null == successResults) {
                    successResults = new HashMap<Slot, Object>();
                    context.put(DSLDefineConstants.SUCCESS_SLOTS_RESULT_MAP, successResults);
                }
                successResults.put(slot, result);
            } catch (Exception e) {
                context.put(DSLDefineConstants.SOME_SLOTS_EXECUTE_FAIL, true);
                context.put(DSLDefineConstants.SOME_SLOTS_EXECUTE_FAIL_EXCEPTION, e);
                context.put(DSLDefineConstants.SOME_SLOTS_EXECUTE_FAIL_SLOT, slot);
                logger.error(slot.toDescription());
                logger.error(e.getMessage(), e);
            }
        }
    }

    private Object invoke(Slot s, Class<?>[] paramsType, Object[] args) {
        Object result = null;
        try {
            result = MethodUtils.invokeMethod(s.getListener(), s.getMethod(), args, paramsType);
        } catch (Exception e) {
            throw new SignalException("listener:" + s.getListener() + " method:" + s.getMethod() + " args:" + s.getArgs() + " sync:"
                    + s.getSync(), e);
        }
        return result;
    }

    class PendingTask implements Runnable {

        private Slot slot;

        private Class<?>[] paramsType;

        private Object[] args;

        public PendingTask(Slot slot, Class<?>[] paramsType, Object[] args) {
            super();
            this.slot = slot;
            this.args = args;
            this.paramsType = paramsType;
        }

        @Override
        public void run() {
            invoke(slot, paramsType, args);
        }
    }

}
