package pro.shushi.pamirs.framework.connectors.data.sql;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.NormalSegmentList;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.common.util.UnsafeUtil;
import pro.shushi.pamirs.meta.constant.FieldConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 条件构造抽象类
 */
@SuppressWarnings("all")
public abstract class Wrapper<T> extends IWrapper<T> implements ISqlSegment {

    /**
     * 实体对象（子类实现）
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    public String getSqlSelect() {
        return null;
    }

    public String getSqlSet() {
        return null;
    }

    public String getSqlComment() {
        return null;
    }

    /**
     * 获取 MergeSegments
     */
    public abstract MergeSegments getExpression();

    /**
     * 获取自定义SQL 简化自定义XML复杂情况
     * <p>使用方法</p>
     * <p>`自定义sql` + ${ew.customSqlSegment}</p>
     * <p>1.逻辑删除需要自己拼接条件 (之前自定义也同样)</p>
     * <p>2.不支持wrapper中附带实体的情况 (wrapper自带实体会更麻烦)</p>
     * <p>3.用法 ${ew.customSqlSegment} (不需要where标签包裹,切记!)</p>
     * <p>4.ew是wrapper定义别名,可自行替换</p>
     */
    public String getCustomSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StringUtils.isNotBlank(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                } else {
                    return SqlConstants.WHERE + CharacterConstants.SEPARATOR_BLANK + sqlSegment;
                }
            }
        }
        return CharacterConstants.SEPARATOR_EMPTY;
    }

    /**
     * 查询条件为空(包含entity)
     */
    public boolean isEmptyOfWhere() {
        return isEmptyOfNormal() && isEmptyOfEntity();
    }

    /**
     * 查询条件不为空(包含entity)
     */
    public boolean nonEmptyOfWhere() {
        return !isEmptyOfWhere();
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean isEmptyOfNormal() {
        return CollectionUtils.isEmpty(getExpression().getNormal());
    }

    /**
     * 查询条件为空(不包含entity)
     */
    public boolean nonEmptyOfNormal() {
        return !isEmptyOfNormal();
    }

    /**
     * 深层实体判断属性
     *
     * @return true 不为空
     */
    public boolean nonEmptyOfEntity() {
        T entity = getEntity();
        if (entity == null) {
            return false;
        }
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(getModel());
        if (modelConfig == null) {
            return false;
        }
        BiFunction<T, String, Boolean> func = fetchEntityMatchFunction(entity);
        if (modelConfig.getModelFieldConfigList().stream().anyMatch(e -> fieldStrategyMatch(entity, e.getLname(), e.getWhereStrategy(), func))) {
            return true;
        }
        return false;
    }

    /**
     * 根据实体FieldStrategy属性来决定判断逻辑
     */
    private boolean fieldStrategyMatch(T entity, String lname, String fieldStrategy, BiFunction<T, String, Boolean> func) {
        FieldStrategyEnum fieldStrategyEnum = Enums.getEnumByValue(FieldStrategyEnum.class, fieldStrategy);
        switch (fieldStrategyEnum) {
            case NOT_NULL:
                return Objects.nonNull(FieldUtils.getFieldValue(entity, lname));
            case IGNORED:
                return true;
            case NOT_EMPTY:
                return PStringUtils.checkValNotNull(FieldUtils.getFieldValue(entity, lname));
            case NEVER:
                return false;
            default:
                return func.apply(entity, lname);
        }
    }

    /**
     * 深层实体判断属性
     *
     * @return true 为空
     */
    public boolean isEmptyOfEntity() {
        return !nonEmptyOfEntity();
    }

    /**
     * 获取判断实体条件是否有效的函数接口
     *
     * @param entity 实体
     * @return
     */
    private BiFunction<T, String, Boolean> fetchEntityMatchFunction(T entity) {
        if (entity instanceof Map) {
            return (e, l) -> ((Map) e).containsKey(l);
        } else if (D.class.isAssignableFrom(entity.getClass())) {
            return (e, l) -> {
                Map<String, Object> _dMap = (Map<String, Object>) UnsafeUtil.getValue(e, FieldConstants._dFieldName);
                if (_dMap == null) {
                    return false;
                }
                return Objects.nonNull(_dMap.get(l));
            };
        } else {
            return (e, l) -> Objects.nonNull(UnsafeUtil.getValue(e, l));
        }
    }

}
