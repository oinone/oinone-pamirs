package pro.shushi.pamirs.meta.dsl.signal;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.dsl.fun.LogicFunInvoker;

import java.util.List;
import java.util.Map;

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
            throw new RuntimeException(currentStateName + "没有找到循环节点" + list + "相对应的值", ex);
        }
        // Object obj = context.get(list);
        if(null == obj){
            throw new RuntimeException(currentStateName + "节点入参集合为空");
        }
        if(!(obj instanceof List)){
            throw new RuntimeException(currentStateName + "节点入参不是集合");
        }
        if(!StringUtils.isNumeric(start) || !StringUtils.isNumeric(end)){
            throw new RuntimeException(currentStateName + "节点入参开始条件和终止条件不是数字，开始条件：" + start + "，结束条件：" + end);
        }
        List l = (List)obj;
        int i = Integer.valueOf(start);
        int e = Integer.valueOf(end);
        currentIndex = null == currentIndex?i:currentIndex;
        if(i < e && currentIndex < e){
            if(currentIndex < l.size()){
                LogicFunInvoker.putResult(context, l.get(currentIndex));
                LogicFunInvoker.putCurrentIndex(context, ++currentIndex);
            }else{
                LogicFunInvoker.putCurrentIndex(context, e);
            }
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
