package pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.constant.StatementConstants;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.InsertMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.constants.OracleScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.util.LogicColumnUtil;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * 通用插入 SQL statement 生成方法
 * <p>
 * 2020/6/16 1:40 下午
 *
 * @author d@shushi.pro
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Data
public class OracleInsertMethod extends AbstractOracleMethod implements InsertMethod {

    private boolean batch = false;

    private String valuePrefix;

    private boolean duplicateKeyUpdate = false;

    public OracleInsertMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }

    @Override
    public String table() {
        return Configs.wrap(getModelConfig()).getTable();
    }

    @Override
    public String sqlInsert() {
        return SqlScriptUtils.convertTrim(getAllInsertSqlColumnMaybeIf(getModelConfig(), valuePrefix, batch),
                CharacterConstants.LEFT_BRACKET, CharacterConstants.RIGHT_BRACKET, null, CharacterConstants.SEPARATOR_COMMA);
    }

    @Override
    public String sqlValues() {
        return SqlScriptUtils.convertTrim(getAllInsertSqlPropertyMaybeIf(getModelConfig(), valuePrefix, batch),
                CharacterConstants.LEFT_BRACKET, CharacterConstants.RIGHT_BRACKET, null, CharacterConstants.SEPARATOR_COMMA);
    }

    @Override
    public String batchSqlValues() {
        return SqlScriptUtils.convertForeach(sqlValues(), Constants.COLLECTION,
                StatementConstants.INDEX, StatementConstants.ITEM, CharacterConstants.SEPARATOR_COMMA);
    }

    @Override
    public String onDuplicateKeyUpdate() {
        if (!duplicateKeyUpdate) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        ModelConfig modelConfig = getModelConfig();
        boolean havePk = modelConfig.havePk();
        String sqlScript = sqlMethodTableFieldConfigList().stream()
                .filter(v -> !havePk || !modelConfig.getPk().contains(v.getField()))
                .filter(v -> null == v.getSequenceConfig())
                .map(v -> getOnDuplicateKeyUpdateMaybeIf(v, valuePrefix, batch))
                .filter(Objects::nonNull)
                .collect(joining(CharacterConstants.NEWLINE));
        sqlScript = LogicColumnUtil.fillOnDuplicateKeyUpdate(modelConfig.getModel(), sqlScript);
        sqlScript = String.format(OracleScriptTemplate.ON_DUPLICATE_KEY_UPDATE, sqlScript);
        return SqlScriptUtils.convertTrim(sqlScript, null, null, null, CharacterConstants.SEPARATOR_COMMA);
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 自动选部位,根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getAllInsertSqlColumnMaybeIf(ModelConfig modelConfig, final String prefix, final boolean batch) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        String script = sqlMethodTableFieldConfigList().stream()
                .filter(modelFieldConfig -> !isAutoIncrementModelField(modelConfig, modelFieldConfig))
                .map(i -> getInsertSqlColumnMaybeIf(i, newPrefix, batch))
                .filter(Objects::nonNull)
                .collect(joining(CharacterConstants.NEWLINE));
        return LogicColumnUtil.fillLogicColumns(modelConfig.getModel(), script);
    }

    /**
     * 获取所有 insert 时候插入值 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * <li> 自动选部位,根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getAllInsertSqlPropertyMaybeIf(ModelConfig modelConfig, final String prefix, final boolean batch) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        String script = sqlMethodTableFieldConfigList().stream()
                .filter(modelFieldConfig -> !isAutoIncrementModelField(modelConfig, modelFieldConfig))
                .map(modelFieldConfig -> getInsertSqlPropertyMaybeIf(modelFieldConfig, newPrefix, batch))
                .filter(Objects::nonNull)
                .collect(joining(CharacterConstants.NEWLINE));
        return LogicColumnUtil.fillInsertLogicProperties(modelConfig.getModel(), script);
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 根据规则会生成 if 标签 </li>
     *
     * @param modelFieldConfig 字段配置
     * @return sql 脚本片段
     */
    public String getInsertSqlColumnMaybeIf(ModelFieldConfig modelFieldConfig, final String prefix, final boolean batch) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        final String sqlScript = getInsertSqlColumn(Configs.wrap(modelFieldConfig).getColumn());
        return convertIf(sqlScript, newPrefix, modelFieldConfig, fieldStrategy(modelFieldConfig, batch, true));
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值) on duplicate key update 更新字段 = values(更新字段), 更新字段2 = values(更新字段2), ..."</p>
     * <p>位于 "on duplicate key update 更新字段1 = values(更新字段1), 更新字段2 = values(更新字段2), ..." 部位</p>
     *
     * <li> 根据规则会生成 if 标签 </li>
     *
     * @param modelFieldConfig 字段配置
     * @return sql 脚本片段
     */
    public String getOnDuplicateKeyUpdateMaybeIf(ModelFieldConfig modelFieldConfig, final String prefix, final boolean batch) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        final String sqlScript = getInsertOrUpdateSqlColumn(modelFieldConfig);
        return convertIf(sqlScript, newPrefix, modelFieldConfig, fieldStrategy(modelFieldConfig, batch, false));
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 不生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlColumn(String column) {
        return column + CharacterConstants.SEPARATOR_COMMA;
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 不生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertOrUpdateSqlColumn(ModelFieldConfig modelFieldConfig) {
        String column = Configs.wrap(modelFieldConfig).getColumn();
        return getInsertOrUpdateSqlColumn(column);
    }

    public static String getInsertOrUpdateSqlColumn(String column) {
        return column + SqlConstants.EQ + SqlConstants.VALUES
                + CharacterConstants.LEFT_BRACKET + column + CharacterConstants.RIGHT_BRACKET
                + CharacterConstants.SEPARATOR_COMMA;
    }

    /**
     * 获取 insert 时候主键 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * @return sql 脚本片段
     */
    public String getKeyInsertSqlColumn(ModelConfig modelConfig, final boolean newLine) {
        if (modelConfig.havePk()) {
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelConfig.getModel());
            String keyGenerator = pamirsTableInfo.getKeyGenerator();
            if (StringUtils.isBlank(keyGenerator)
                    || keyGenerator.equals(KeyGeneratorEnum.NON.value())
                    || keyGenerator.equals(KeyGeneratorEnum.AUTO_INCREMENT.value())) {
                return CharacterConstants.SEPARATOR_EMPTY;
            }
            return StringUtils.join(modelConfig.getPk(), CharacterConstants.SEPARATOR_COMMA)
                    + CharacterConstants.SEPARATOR_COMMA + (newLine ? CharacterConstants.NEWLINE : CharacterConstants.SEPARATOR_EMPTY);
        }
        return CharacterConstants.SEPARATOR_EMPTY;
    }

    /**
     * 获取 insert 时候主键 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * @return sql 脚本片段
     */
    public String getKeyInsertSqlProperty(ModelConfig modelConfig, final String prefix, final boolean newLine) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        if (modelConfig.havePk()) {
            PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelConfig.getModel());
            String keyGenerator = pamirsTableInfo.getKeyGenerator();
            if (StringUtils.isBlank(keyGenerator)
                    || keyGenerator.equals(KeyGeneratorEnum.NON.value())
                    || keyGenerator.equals(KeyGeneratorEnum.AUTO_INCREMENT.value())) {
                return CharacterConstants.SEPARATOR_EMPTY;
            }
            return StringUtils.join(modelConfig.getPk().stream().map(v -> SqlScriptUtils.safeParam(newPrefix + v)).collect(Collectors.toList()), CharacterConstants.SEPARATOR_COMMA)
                    + CharacterConstants.SEPARATOR_COMMA + (newLine ? CharacterConstants.NEWLINE : CharacterConstants.SEPARATOR_EMPTY);
        }
        return CharacterConstants.SEPARATOR_EMPTY;
    }

    /**
     * 获取 insert 时候插入值 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * <li> 根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlPropertyMaybeIf(ModelFieldConfig modelFieldConfig, final String prefix, final boolean batch) {
        String sqlScript = getInsertSqlProperty(modelFieldConfig.getLname(), prefix);
        final String insertColumn = getInsertSqlColumn(Configs.wrap(modelFieldConfig).getColumn());
        return convertIf(sqlScript, insertColumn, prefix, modelFieldConfig, fieldStrategy(modelFieldConfig, batch, true));
    }

    /**
     * 获取 insert 时候插入值 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * <li> 不生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getInsertSqlProperty(String property, final String prefix) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        return SqlScriptUtils.safeParam(newPrefix + property) + CharacterConstants.SEPARATOR_COMMA;
    }

    private String fieldStrategy(ModelFieldConfig modelFieldConfig, final boolean batch, final boolean insert) {
        return batch ?
                (
                        FieldStrategyEnum.NEVER.value().equals(modelFieldConfig.getBatchStrategy()) ? FieldStrategyEnum.NEVER.value() : FieldStrategyEnum.NOT_CHANGE.value()
                )
                :
                (
                        insert ?
                                null == modelFieldConfig.getInsertStrategy() ? FieldStrategyEnum.DEFAULT.value() : modelFieldConfig.getInsertStrategy()
                                :
                                null == modelFieldConfig.getUpdateStrategy() ? FieldStrategyEnum.DEFAULT.value() : modelFieldConfig.getUpdateStrategy()
                );
    }

}
