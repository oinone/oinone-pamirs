package pro.shushi.pamirs.meta.api.dto.condition;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * 排序字段
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base
@Model.model("base.Order")
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "排序字段", summary = "排序字段")
public class Order extends TransientModel {

    private static final long serialVersionUID = -3028069425484313020L;

    @Base
    @Field(displayName = "排序方式")
    private SortDirectionEnum direction;

    @Base
    @Field(displayName = "排序字段")
    private String field;

    public Order() {
    }

    public Order(SortDirectionEnum direction, String field) {
        this.setDirection(direction);
        this.setField(field);
    }

    public SortDirectionEnum getDirection() {
        if (this._d.get("direction") instanceof String) {
            return SortDirectionEnum.valueOf(TypeUtils.stringValueOf(this._d.get("direction")));
        }
        return (SortDirectionEnum) this._d.get("direction");
    }

    public Order setDirection(SortDirectionEnum direction) {
        this._d.put("direction", direction);
        return this;
    }

}
