package pro.shushi.pamirs.meta.api.dto.crud;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排序
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base
@Model.model("base.Sort")
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "排序", summary = "排序")
public class Sort extends TransientModel {

    private static final long serialVersionUID = -6905445517733342682L;

    @Base
    @Field.one2many
    @Field(displayName = "排序字段")
    private List<Order> orders;

    public Sort(){
        this.setOrders(new ArrayList<>());
    }

    public Sort(List<Order> orders){
        this.setOrders(orders);
    }

    public Sort createOrders (Map sortMap) {
        List orders = (List) sortMap.get("orders");
        this.setOrders((List<Order>) orders.stream().map(v -> new Order(SortDirectionEnum.valueOf((String) ((Map) v).get("direction")), (String) ((Map) v).get("field"))).collect(Collectors.toList()));
        return this;
    }

    public Sort addOrder(Order order) {
        this.getOrders().add(order);
        return this;
    }

}
