package pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.SelectMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.constants.OracleScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.util.LogicColumnUtil;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.framework.connectors.data.sql.config.ModelFieldConfigWrapper;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Constants.*;
import static java.util.stream.Collectors.joining;
import static pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants.*;
import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.*;
import static pro.shushi.pamirs.framework.connectors.data.mapper.template.ScriptTemplate.BRACKET;
import static pro.shushi.pamirs.meta.common.constants.CharacterConstants.NEWLINE;

/**
 * 通用更新 SQL statement 生成方法
 * <p>
 * 2020/6/16 1:40 下午
 *
 * @author d@shushi.pro
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Data
public class OracleSelectMethod extends AbstractOracleMethod implements SelectMethod {

    private String pkPrefix;

    private String optimisticLockerPrefix;

    private boolean onlyColumn = false;

    private boolean keyConflict = false;

    private boolean withPk;

    private boolean queryWrapper = false;

    private boolean useOptimisticLocker = false;

    private String[] nonEmptyUniqueKey;

    public OracleSelectMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }

    @SuppressWarnings("unused")
    @Override
    public String table() {
        return Configs.wrap(getModelConfig()).getTable();
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlCount() {
        if (!queryWrapper) {
            return ONE;
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), ONE);
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlSelect() {
        /* 假设存在用户自定义的 resultMap 映射返回 */
        String selectColumns = chooseSelect(onlyColumn);
        if (!queryWrapper) {
            return selectColumns;
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), selectColumns);
    }

    public String logicSegment() {
        String model = getModelConfig().getModel();
        String script = CharacterConstants.SEPARATOR_EMPTY;
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        boolean logicDelete = pamirsTableInfo.getLogicDelete();
        if (logicDelete) {
            script = OracleScriptTemplate.LOGIC_DELETE;
            script += NEWLINE;
        }
        String optimisticLockerField = getModelConfig().getOptimisticLockerField();
        if (useOptimisticLocker && StringUtils.isNotBlank(optimisticLockerField)) {
            script += "AND " + String.format(OracleScriptTemplate.OPTIMISTIC_LOCKER,
                    optimisticLockerPrefix.substring(0, optimisticLockerPrefix.length() - 1));
            script += NEWLINE;
        }
        return LogicColumnUtil.fillSqlSegment(model, script);
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlSegment() {
        String logicSegment = logicSegment();
        String sqlScript = CharacterConstants.SEPARATOR_EMPTY;
        if (!queryWrapper) {
            sqlScript += getAllSqlWhere(withPk, ENTITY_DOT);
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", ENTITY), true);
            sqlScript = logicSegment + SqlScriptUtils.convertTrim(sqlScript, AND.toUpperCase(), null, AND.toUpperCase(), null);
            sqlScript = SqlScriptUtils.convertWhere(sqlScript) + NEWLINE;
            return sqlScript;
        }
        sqlScript += getAllSqlWhere(withPk, WRAPPER_ENTITY_DOT);
        sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", WRAPPER_ENTITY), true);
        sqlScript += NEWLINE;
        sqlScript += SqlScriptUtils.convertIf(String.format(SqlScriptUtils.convertIf(" AND", String.format("%s and %s", WRAPPER_NONEMPTYOFENTITY, WRAPPER_NONEMPTYOFNORMAL), false) + " ${%s}", WRAPPER_SQLSEGMENT),
                String.format("%s != null and %s != '' and %s", WRAPPER_SQLSEGMENT, WRAPPER_SQLSEGMENT,
                        WRAPPER_NONEMPTYOFWHERE), true);
        sqlScript = logicSegment + SqlScriptUtils.convertTrim(sqlScript, AND.toUpperCase(), null, AND.toUpperCase(), null);
        sqlScript = SqlScriptUtils.convertWhere(sqlScript) + NEWLINE;
        sqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", WRAPPER_SQLSEGMENT),
                String.format("%s != null and %s != '' and %s", WRAPPER_SQLSEGMENT, WRAPPER_SQLSEGMENT,
                        WRAPPER_EMPTYOFWHERE), true);
        sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", WRAPPER), true);
        return sqlScript;
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlPk() {
        String sqlScript = logicSegment();
        sqlScript += getPkSqlWhere(pkPrefix);
        sqlScript = SqlScriptUtils.convertWhere(sqlScript) + NEWLINE;
        return sqlScript;
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlPks() {
        List<String> pks = getModelConfig().getPk();
        if (CollectionUtils.isEmpty(pks)) {
            throw PamirsException.construct(BASE_NO_PK_CONFIG_ERROR).errThrow();
        }
        List<ModelFieldConfig> modelFieldConfigList = new ArrayList<>();
        String model = getModelConfig().getModel();
        for (String pk : pks) {
            ModelFieldConfig pkConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelField(model, pk)).orElse(null);
            if (null == pkConfig) {
                throw PamirsException.construct(BASE_NO_PK_CONFIG2_ERROR).errThrow();
            }
            modelFieldConfigList.add(pkConfig);
        }
        return sqlComplexes(modelFieldConfigList);
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlUnique() {
        String sqlScript = logicSegment();
        sqlScript += getUniqueKeySqlWhere(ENTITY_DOT);
        sqlScript = SqlScriptUtils.convertWhere(sqlScript) + NEWLINE;
        return sqlScript;
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlUniques() {
        Set<String> uniques = Arrays.stream(nonEmptyUniqueKey).collect(Collectors.toSet());
        List<ModelFieldConfig> modelFieldConfigList = new ArrayList<>();
        String model = getModelConfig().getModel();
        for (String uniqueKey : uniques) {
            ModelFieldConfig uniqueFieldConfig = Optional.ofNullable(PamirsSession.getContext()).map(v -> v.getModelField(model, uniqueKey)).orElse(null);
            if (null == uniqueFieldConfig) {
                throw PamirsException.construct(BASE_NO_UNIQUE_KEY_FIELD_CONFIG_ERROR).errThrow();
            }
            modelFieldConfigList.add(uniqueFieldConfig);
        }
        return sqlComplexes(modelFieldConfigList);
    }

    @SuppressWarnings("unused")
    public String sqlComplexes(List<ModelFieldConfig> modelFieldConfigList) {
        boolean singleField = 1 == modelFieldConfigList.size();
        String columns = modelFieldConfigList.stream()
                .map(i -> ModelFieldConfigWrapper.wrap(i).getSqlSelect()).collect(joining(COMMA));
        String values = modelFieldConfigList.stream()
                .map(i -> SqlScriptUtils.safeParam(StatementConstants.ITEM_DOT + i.getLname())).collect(joining(COMMA));
        if (!singleField) {
            columns = String.format(BRACKET, columns);
            values = String.format(BRACKET, values);
        }

        String sqlScript = logicSegment();
        sqlScript += String.format(ScriptTemplate.IN_CONDITION, columns, SqlScriptUtils.convertForeach(values, CONDITION_COLLECTION, INDEX, ITEM, COMMA));
        sqlScript = SqlScriptUtils.convertWhere(sqlScript) + NEWLINE;
        return sqlScript;
    }

    /**
     * 获取需要进行查询的 select sql 片段
     *
     * @return sql 片段
     */
    public String chooseSelect() {
        return chooseSelect(true);
    }

    public String chooseSelect(boolean onlyColumn) {
        String sqlSelect = CharacterConstants.SEPARATOR_ASTERISK;
        String fieldsSqlSelect = sqlMethodTableFieldConfigList().stream()
                .map(i -> ModelFieldConfigWrapper.wrap(i).getSqlSelect(onlyColumn)).collect(joining(COMMA));
        if (StringUtils.isNotBlank(fieldsSqlSelect)) {
            return fieldsSqlSelect;
        }
        return sqlSelect;
    }

    /**
     * 获取所有的查询的 sql 片段
     *
     * @param withPk 是否包含 主键 项
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getAllSqlWhere(boolean withPk, final String prefix) {
        ModelConfig modelConfig = getModelConfig();
        final String newPrefix = prefix == null ? EMPTY : prefix;
        return sqlMethodTableFieldConfigList().stream()
                .filter(ModelFieldConfig::getStore)
                .filter(v -> withPk || !modelConfig.havePk() || !modelConfig.getPk().contains(v.getField())
                        || (null == v.getOptimisticLocker() || !v.getOptimisticLocker()))
                .map(i -> getSqlWhere(i, newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取主键的查询的 sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getPkSqlWhere(final String prefix) {
        ModelConfig modelConfig = getModelConfig();
        final String newPrefix = prefix == null ? EMPTY : prefix;
        boolean havePk = modelConfig.havePk();
        if (!havePk) {
            throw PamirsException.construct(BASE_NO_PK_CONFIG3_ERROR).errThrow();
        }
        return sqlMethodTableFieldConfigList().stream()
                .filter(v -> modelConfig.getPk().contains(v.getField()))
                .map(i -> getSqlWhereWithoutIf(i, i.getOptimisticLocker() ? optimisticLockerPrefix : newPrefix))
                .filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取唯一索引的查询的 sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getUniqueKeySqlWhere(final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        Set<String> nonEmptyUniqueKeySet = Arrays.stream(nonEmptyUniqueKey).collect(Collectors.toSet());
        return sqlMethodTableFieldConfigList().stream()
                .filter(v -> nonEmptyUniqueKeySet.contains(v.getLname()))
                .filter(ModelFieldConfig::getStore)
                .map(i -> getSqlWhere(i, newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取 查询的 sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getSqlWhere(ModelFieldConfig modelFieldConfig, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        String sqlScript = getSqlWhereWithoutIf(modelFieldConfig, newPrefix);
        // 查询的时候只判非空
        return convertIf(sqlScript, newPrefix, modelFieldConfig, modelFieldConfig.getWhereStrategy());
    }

    public String getSqlWhereWithoutIf(ModelFieldConfig modelFieldConfig, final String prefix) {
        // 默认:  AND column=#{prefix + el}
        String column = Configs.wrap(modelFieldConfig).getColumn();
        return " AND " + String.format(modelFieldConfig.getWhereCondition(), column, prefix + fetchNameConflict(modelFieldConfig.getLname()));
    }

    @Override
    public boolean isNameConflict(String name) {
        return isKeyConflict() && DataMap.CONFLICT_KEYS.contains(name);
    }

    public static boolean isWrapperEntityMap(IWrapper<?> iWrapper) {
        if (null == iWrapper) {
            return false;
        }
        return isEntityMap(iWrapper.getEntity());
    }

    public static <T> boolean isEntityMap(T entity) {
        return entity instanceof Map;
    }

}
