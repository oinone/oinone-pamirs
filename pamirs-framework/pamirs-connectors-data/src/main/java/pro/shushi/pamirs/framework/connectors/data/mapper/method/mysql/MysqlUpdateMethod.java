package pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.UpdateMethod;
import pro.shushi.pamirs.framework.connectors.data.sql.config.Configs;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;

import java.util.Objects;

import static com.baomidou.mybatisplus.core.toolkit.Constants.U_WRAPPER_SQL_SET;
import static com.baomidou.mybatisplus.core.toolkit.Constants.WRAPPER;
import static java.util.stream.Collectors.joining;
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
public class MysqlUpdateMethod extends MysqlSelectMethod implements UpdateMethod {

    private String valuePrefix;

    private boolean judgeEntityNull = true;

    private boolean wrapperSet = true;

    private boolean batch = false;

    public MysqlUpdateMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }

    @SuppressWarnings("unused")
    @Override
    public String sqlSet() {
        String sqlScript = getAllSqlSet(getModelConfig(), valuePrefix);
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(getModelConfig().getModel());
        if (judgeEntityNull) {
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", Constants.ENTITY), true);
        }
        if (isWrapperSet()) {
            sqlScript += NEWLINE;
            sqlScript += SqlScriptUtils.convertIf(SqlScriptUtils.unSafeParam(U_WRAPPER_SQL_SET),
                    String.format("%s != null and %s != null", WRAPPER, U_WRAPPER_SQL_SET), false);
        }
        sqlScript = SqlScriptUtils.convertSet(sqlScript);
        return sqlScript;
    }

    /**
     * 获取所有的 sql set 片段
     *
     * @param modelConfig 模型配置
     * @param prefix      前缀
     * @return sql 脚本片段
     */
    public String getAllSqlSet(ModelConfig modelConfig, final String prefix) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        boolean havePk = modelConfig.havePk();
        boolean relationship = modelConfig.getRelationship();
        boolean needUpdate = !havePk || relationship;
        return sqlMethodTableFieldConfigList().stream()
                .filter(v -> (null == v.getImmutable() || !v.getImmutable()) && (needUpdate || !modelConfig.getPk().contains(v.getField())))
                .map(i -> getSqlSet(i, false, i.getOptimisticLocker() ? getOptimisticLockerPrefix() : newPrefix, null))
                .filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取 set sql 片段
     *
     * @param ignoreIf 忽略 IF 包裹
     * @param prefix   前缀
     * @return sql 脚本片段
     */
    public String getSqlSet(ModelFieldConfig modelFieldConfig, final boolean ignoreIf, final String prefix, String updatePattern) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        // 默认: column=
        String column = Configs.wrap(modelFieldConfig).getColumn();
        String sqlSet = column + SqlConstants.EQ;
        if (StringUtils.isNotBlank(updatePattern)) {
            sqlSet += String.format(updatePattern, column);
        } else {
            sqlSet += SqlScriptUtils.safeParam(newPrefix + modelFieldConfig.getLname());
        }
        sqlSet += CharacterConstants.SEPARATOR_COMMA;
        if (ignoreIf) {
            return sqlSet;
        }
        return convertIf(sqlSet, newPrefix, modelFieldConfig, modelFieldConfig.getUpdateStrategy());
    }

    public String getSqlSet(String column, final String prefix, String updatePattern) {
        final String newPrefix = prefix == null ? CharacterConstants.SEPARATOR_EMPTY : prefix;
        // 默认: column=
        String sqlSet = column + SqlConstants.EQ;
        if (StringUtils.isNotBlank(updatePattern)) {
            sqlSet += String.format(updatePattern, column);
        } else {
            sqlSet += SqlScriptUtils.safeParam(newPrefix + column);
        }
        sqlSet += CharacterConstants.SEPARATOR_COMMA;
        return sqlSet;
    }

}
