package pro.shushi.pamirs.meta.base;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 没有字段的空模型，基类
 *
 * 无名万物之始也有名万物之母也
 *
 * @author huidao(d@shushi.pro)
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@pro.shushi.pamirs.meta.annotation.fun.D
public abstract class D implements Serializable {

    /**
     * 模型的真正数据容器
     */
    @JSONField(serialize=false)
    protected Map<String, Object> _d = new HashMap<>();

    /**
     * 无字段常量
     */
    protected final String NO_FIELDS = "";

}
