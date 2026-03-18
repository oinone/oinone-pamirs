package pro.shushi.pamirs.meta.dsl.signal;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.dsl.enumeration.DslExpEnumerate.*;

public class Iterator implements Exe {

    private String list;

    private String start;

    private String end;

    private String step;

    @Override
    public void dispatch(Map<String, Object> context) {
        String currentStateName = LogicFunInvoker.fetchCurrentStateName(context);
        Integer currentIndex = LogicFunInvoker.fetchCurrentIndex(context);
        Object obj = null;
        try {
            obj = LogicFunInvoker.exp(list, context);
        } catch (Exception ex) {
            throw PamirsException.construct(BASE_FOREACH_HANDLE_ERROR, ex)
                    .appendMsg(I18nUtils.getMessage("pamirs.meta.dsl.signal.iterator.valueNotFound", currentStateName, list)).errThrow();
        }
        // Object obj = context.get(list);
        if (null == obj) {
            throw PamirsException.construct(BASE_FOREACH_PARAMS_IS_EMPTY_ERROR)
                    .appendMsg(I18nUtils.getMessage("pamirs.meta.dsl.signal.iterator.collectionEmpty", currentStateName)).errThrow();
        }
        if (!(obj instanceof List)) {
            throw PamirsException.construct(BASE_FOREACH_PARAMS_IS_NOT_COLLECTION_ERROR)
                    .appendMsg(I18nUtils.getMessage("pamirs.meta.dsl.signal.iterator.notCollection", currentStateName)).errThrow();
        }
        if (!StringUtils.isNumeric(start) || !StringUtils.isNumeric(end)) {
            throw PamirsException.construct(BASE_FOREACH_PARAMS_ERROR)
                    .appendMsg(I18nUtils.getMessage("pamirs.meta.dsl.signal.iterator.startEndNotNumeric", currentStateName, start, end)).errThrow();
        }
        List l = (List) obj;
        int i = Integer.valueOf(start);
        int e = Integer.valueOf(end);
        if (i >= e) {
            throw PamirsException.construct(BASE_FOREACH_PARAMS_ERROR)
                    .appendMsg(I18nUtils.getMessage("pamirs.meta.dsl.signal.iterator.startEndConfigError", currentStateName, start, end)).errThrow();
        }

        currentIndex = null == currentIndex ? i : currentIndex + 1;
        if (currentIndex < e && currentIndex < l.size()) {
            LogicFunInvoker.putResult(context, l.get(currentIndex));
            LogicFunInvoker.putCurrentIndex(context, currentIndex);
        } else {
            LogicFunInvoker.putCurrentIndex(context, e);
        }
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

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

}
