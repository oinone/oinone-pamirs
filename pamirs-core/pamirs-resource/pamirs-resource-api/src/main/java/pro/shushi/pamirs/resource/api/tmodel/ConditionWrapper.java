package pro.shushi.pamirs.resource.api.tmodel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.core.common.tmodel.CommonConditionWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Order;
import pro.shushi.pamirs.meta.api.dto.condition.Sort;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.RSqlConstants;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.util.List;
import java.util.Map;

/**
 * @deprecated 6.x please using {@link CommonConditionWrapper}
 */
@Slf4j
@Base
@Model.model(ConditionWrapper.MODEL_MODEL)
@Model(displayName = "查询条件")
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
        String model = getModel();
        QueryWrapper<T> wrapper = Pops.query();
        wrapper.setQueryData(getQueryData());
        wrapper.from(model).setRsql(getRsql());
        withOrderBy(wrapper);
        return wrapper;
    }

    public <T> void withOrderBy(QueryWrapper<T> wrapper) {
        String model = getModel();
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
                    ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, field);
                    if (modelFieldConfig == null) {
                        throw PamirsException.construct(CommonExpEnumerate.SORT_FIELD_NOT_FOUND, model, field).errThrow();
                    }
                    String column = Configs.wrap(modelFieldConfig).getColumn();
                    if (StringUtils.isBlank(column)) {
                        log.error("sort field column is blank. model: {}, field: {}", model, field);
                        continue;
                    }
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
