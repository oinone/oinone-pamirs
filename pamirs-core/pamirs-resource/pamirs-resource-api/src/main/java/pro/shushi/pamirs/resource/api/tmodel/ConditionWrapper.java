package pro.shushi.pamirs.resource.api.tmodel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.RSqlConstants;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.util.List;
import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-03 15:41
 */
@Base
@Model.model(ConditionWrapper.MODEL_MODEL)
@Model(displayName = "导出查询条件")
public class ConditionWrapper extends TransientModel {

    private static final long serialVersionUID = 8483736574402915828L;

    public static final String MODEL_MODEL = "resource.ConditionWrapper";

    @Field.String
    @Field(displayName = "模型编码")
    private String model;

    @Field.String
    @Field(displayName = "rsql")
    private String rsql;

    @Base
    @Field.many2one
    @Field(displayName = "排序")
    private Sort sort;

    /**
     * 传输数据实体，传输引用字段、传输字段数据
     */
    @Base
    @Field(displayName = "传输数据")
//    private transient Map<String, Object> queryData;
    private Map<String, Object> queryData;

    public <T> QueryWrapper<T> generatorQueryWrapper() {
        QueryWrapper<T> wrapper = Pops.query();
        wrapper.setQueryData(getQueryData());
        wrapper.from(getModel()).setRsql(getRsql());

        Sort sort = getSort();
        if (sort != null) {
            List<Order> orders = sort.getOrders();
            if (CollectionUtils.isNotEmpty(orders)) {
                for (Order order : orders) {
                    String field = order.getField();
                    SortDirectionEnum direction = order.getDirection();
                    if (StringUtils.isBlank(field) || direction == null) {
                        continue;
                    }
                    String column = PStringUtils.fieldName2Column(field);
                    switch (direction) {
                        case ASC:
                            wrapper.orderByAsc(column);
                            break;
                        case DESC:
                            wrapper.orderByDesc(column);
                            break;
                        default:
                            throw new UnsupportedOperationException("Invalid sort direction enumeration.");
                    }
                }
            }
        }
        return wrapper;
    }

    public String and(String extend) {
        String originRSQL = this.getRsql();
        String s;
        if (StringUtils.isBlank(originRSQL)) {
            s = extend;
        } else if (StringUtils.isBlank(extend)) {
            s = originRSQL;
        } else {
            s = RSqlConstants.LEFT_PARENTHESES + originRSQL + RSqlConstants.RIGHT_PARENTHESES + CharacterConstants.SEPARATOR_BLANK + RSqlConstants.AND + CharacterConstants.SEPARATOR_BLANK + extend;
        }
        return s;
    }
}