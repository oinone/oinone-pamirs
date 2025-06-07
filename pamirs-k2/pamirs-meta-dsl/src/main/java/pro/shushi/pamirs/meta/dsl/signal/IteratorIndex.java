package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.definition.node.Break;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.Map;

/**
 * @author drome
 * @date 2021/10/264:52 下午
 */
public class IteratorIndex implements Exe {

    private String foreachId;

    private Integer optType;

    @Override
    public void dispatch(Map<String, Object> context) {
        if (Break.INDEX_OPT_TYPE.equals(optType)) {
            LogicFunInvoker.foreachBreak(context, foreachId);
        } else {
            // 继续执行,index不用改
        }
    }

    public String getForeachId() {
        return foreachId;
    }

    public void setForeachId(String foreachId) {
        this.foreachId = foreachId;
    }

    public Integer getOptType() {
        return optType;
    }

    public void setOptType(Integer optType) {
        this.optType = optType;
    }
}
