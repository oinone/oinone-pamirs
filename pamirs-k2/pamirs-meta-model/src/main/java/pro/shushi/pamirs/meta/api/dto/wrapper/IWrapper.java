package pro.shushi.pamirs.meta.api.dto.wrapper;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper.MODEL_MODEL;

/**
 * 持久化包装类
 * <p>
 * 2020/6/17 11:25 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "查询条件")
public abstract class IWrapper<T> extends TransientModel {

    private static final long serialVersionUID = 4889788207720967773L;

    public final static String MODEL_MODEL = "base.Condition";

    private String model;

    /**
     * 实体对象（子类实现）
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    /**
     * 泛化查询实体
     *
     * @param entity 实体对象
     * @return 泛化后的查询对象
     */
    public abstract IWrapper<DataMap> generic(DataMap entity);

    /**
     * 泛化查询实体
     *
     * @param model  模型编码
     * @param entity 实体对象
     * @return 泛化后的查询对象
     */
    public abstract IWrapper<DataMap> generic(String model, DataMap entity);

    /**
     * 查询时分批次查询数量
     */
    public int getBatchSize() {
        return 0;
    }

    /**
     * 是否需要排序,默认排序
     */
    public Boolean getSortable() {
        return Boolean.TRUE;
    }

    /**
     * 查询字段或函数，只供端到端api协议传输使用
     */
    @Base
    @Field(displayName = "属性选择")
    private transient List<String> selects;

    /**
     * rsql查询协议，只供端到端api协议传输使用
     */
    @Base
    @Field.Advanced(name = "rsql")
    @Field.field("rsql")
    @Field(displayName = "rsql表达式")
    private transient String rsql;

    private String originRsql;

    /**
     * 传输数据实体，传输引用字段、传输字段数据
     */
    @Base
    @Field(displayName = "传输数据")
    private transient Map<String, Object> queryData;

}
