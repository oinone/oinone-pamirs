package pro.shushi.pamirs.meta.api.dto.crud;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 查询条件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base
@Model.model("base.QueryCondition")
@Model.Advanced(name = "condition", type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "查询条件", summary = "查询条件")
public class QueryCondition<T> extends Condition<T> {

    private static final long serialVersionUID = 8168201166559900886L;

    private String[] select;

    public QueryCondition(Class<T> modelClazz) {
        super(modelClazz);
        constructObject();
    }

    public QueryCondition(String model) {
        super(model);
        this.setModel(model);
        constructObject();
    }

    private void constructObject(){
        this.setAggs(CharacterConstants.SEPARATOR_EMPTY);
    }

    public QueryCondition<T> setAggs(String aggs) {
        this._d.put("aggs", aggs);
        return this;
    }

    @SuppressWarnings("unused")
    public QueryCondition<T> select(String... columns){
        this.setSelect(columns);
        return this;
    }

}
