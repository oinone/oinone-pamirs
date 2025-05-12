package pro.shushi.pamirs.meta.base;

import org.apache.commons.collections.MapUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.List;
import java.util.Map;

/**
 * 泛化抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
public class GenericModel extends AbstractModel {

    private static final long serialVersionUID = -6995051156256398228L;

    @SuppressWarnings("unused")
    public GenericModel(String model) {
        Models.api().setModel(this, model);
    }

    public GenericModel(String model, Map<String, Object> data) {
        if (MapUtils.isNotEmpty(data)) {
            this.set_d(data);
        }
        Models.api().setModel(this, model);
    }

    public GenericModel setValue(String fieldName, Object value) {
        this.get_d().put(fieldName, value);
        return this;
    }

    public <T> GenericModel setValue(Getter<T, ?> getter, Object value) {
        setValue(LambdaUtil.fetchFieldName(getter), value);
        return this;
    }

    public Object getValue(String fieldName) {
        return this.get_d().get(fieldName);
    }

    public <T> Object getValue(Getter<T, ?> getter) {
        return getValue(LambdaUtil.fetchFieldName(getter));
    }

    /**
     * 为模型数据列表填充默认值
     * <br/>
     * 为值为空的字段填充默认值
     *
     * @param model 模型编码
     * @param <T>   模型类型或者Map
     * @return 模型数据或者Map列表
     */
    public static <T> List<T> construct(String model, List<T> dataList) {
        Models.api().setModel(dataList, model);
        return constructor.construct(dataList);
    }

}
