package pro.shushi.pamirs.meta.api.dto.crud;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.ModelModelFetcher;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 列定义
 *
 * 2020/3/2 11:51 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Base
@Model.model("base.Condition")
@Model(displayName = "查询条件")
public class Condition<T> extends TransientModel {

    private static final long serialVersionUID = 4262810042167849817L;

    @Base
    @Field.Advanced(name = "rsql")
    @Field.field("rsql")
    @Field(displayName = "rsql表达式")
    private String where;

    private T whereEntity;

    private Object wrapper;

    private String model;

    public Condition(Class<T> modelClazz) {
        super();
        this.setModel(MetaApiFactory.getApi(ModelModelFetcher.class).getModel(modelClazz));
    }

    public Condition(String model) {
        super();
        this.setModel(model);
    }

    public Condition<T> where(T whereEntity){
        this.setWhereEntity(whereEntity);
        return this;
    }

    @SuppressWarnings("unused")
    public Condition<T> wrapper(Object wrapper){
        this.setWrapper(wrapper);
        return this;
    }

}
