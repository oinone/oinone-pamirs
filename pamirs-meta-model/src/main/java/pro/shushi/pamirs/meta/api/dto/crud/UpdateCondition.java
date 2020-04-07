package pro.shushi.pamirs.meta.api.dto.crud;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 更新条件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base
@Model.model("base.UpdateCondition")
@Model(displayName = "更新条件")
public class UpdateCondition<T> extends Condition<T> {

    private static final long serialVersionUID = -4669617430859433357L;

    private String set;

    private T updateEntity;

    public UpdateCondition(Class<T> modelClazz){
        super(modelClazz);
    }

    public UpdateCondition(String model){
        super(model);
    }

    public UpdateCondition<T> update(T updateEntity){
        this.setUpdateEntity(updateEntity);
        return this;
    }

}
