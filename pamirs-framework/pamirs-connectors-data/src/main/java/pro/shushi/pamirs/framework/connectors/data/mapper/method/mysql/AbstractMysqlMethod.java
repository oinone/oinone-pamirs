package pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.AbstractMethod;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

/**
 * 通用 SQL Statement  生成方法抽象类
 * <p>
 * 2020/6/16 1:39 下午
 *
 * @author d@shushi.pro
 * @author wx@shushi.pro
 * @version 1.0.0
 */
public class AbstractMysqlMethod extends AbstractMethod {

    public AbstractMysqlMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }

    /**
     * 转换成 if 标签的脚本片段
     *
     * @param sqlScript        sql 脚本片段
     * @param prefix           前缀
     * @param modelFieldConfig 字段配置
     * @param fieldStrategy    验证策略
     * @return if 脚本片段
     */
    public String convertIf(final String sqlScript, String prefix, final ModelFieldConfig modelFieldConfig, final String fieldStrategy) {
        if (FieldStrategyEnum.NEVER.value().equals(fieldStrategy)) {
            return null;
        }
        if (FieldStrategyEnum.IGNORED.value().equals(fieldStrategy) || FieldStrategyEnum.NOT_CHANGE.value().equals(fieldStrategy)) {
            return sqlScript;
        }
        if (FieldStrategyEnum.DEFAULT.value().equals(fieldStrategy)) {
            String judgeCondition = judgeCondition(prefix, modelFieldConfig);
            return SqlScriptUtils.convertIf(sqlScript, judgeCondition, false);
        }
        String property = prefix + fetchNameConflict(modelFieldConfig.getLname());
        if (FieldStrategyEnum.NOT_EMPTY.value().equals(fieldStrategy) && TypeUtils.isStringType(modelFieldConfig.getLtype())) {
            //TODO
            return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and %s != ''", property, property),
                    false);
        }
        return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
    }

    /**
     * 转换成 choose 标签的脚本片段
     *
     * @param whenSqlScript      when 脚本片段
     * @param otherSqlScript    other 脚本片段
     * @param prefix           前缀
     * @param modelFieldConfig 字段配置
     * @param fieldStrategy    验证策略
     * @return if 脚本片段
     */
    public String convertChoose(final String whenSqlScript, final String otherSqlScript, String prefix, final ModelFieldConfig modelFieldConfig, final String fieldStrategy) {
        if (FieldStrategyEnum.NEVER.value().equals(fieldStrategy)) {
            return null;
        }
        if (FieldStrategyEnum.IGNORED.value().equals(fieldStrategy) || FieldStrategyEnum.NOT_CHANGE.value().equals(fieldStrategy)) {
            return whenSqlScript;
        }
        if (FieldStrategyEnum.DEFAULT.value().equals(fieldStrategy)) {
            String judgeCondition = judgeCondition(prefix, modelFieldConfig);
            return SqlScriptUtils.convertChoose(judgeCondition, whenSqlScript, otherSqlScript);
        }
        String property = prefix + fetchNameConflict(modelFieldConfig.getLname());
        if (FieldStrategyEnum.NOT_EMPTY.value().equals(fieldStrategy) && TypeUtils.isStringType(modelFieldConfig.getLtype())) {
            //TODO
            return SqlScriptUtils.convertChoose(String.format("%s != null and %s != ''", property, property), whenSqlScript, otherSqlScript);
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null", property), whenSqlScript, otherSqlScript);
    }

    /**
     * 转换成 if 标签的脚本片段
     *
     * @param sqlScript        sql 脚本片段
     * @param prefix           前缀
     * @param modelFieldConfig 字段配置
     * @param fieldStrategy    验证策略
     * @return if 脚本片段
     */
    public String convertIf(final String sqlScript, final String optionSqlScript, String prefix, final ModelFieldConfig modelFieldConfig, final String fieldStrategy) {
        if (FieldStrategyEnum.NOT_CHANGE.value().equals(fieldStrategy)) {
            String judgeCondition = judgeCondition(prefix, modelFieldConfig);
            String script = SqlScriptUtils.convertIf(sqlScript, judgeCondition, false);
            return script + SqlScriptUtils.convertIf(optionSqlScript, "!" + judgeCondition, false);
        } else {
            return convertIf(sqlScript, prefix, modelFieldConfig, fieldStrategy);
        }
    }

    protected String judgeCondition(String prefix, ModelFieldConfig modelFieldConfig) {
        return FieldUtil.NAME + ".containsKey(" + prefix.substring(0, prefix.length() - 1) + ", '" + fetchNameConflict(modelFieldConfig.getLname()) + "')";
    }

    protected String convertIfProperty(String prefix, String property) {
        return StringUtils.isNotBlank(prefix) ? prefix.substring(0, prefix.length() - 1) + "['" + property + "']" : property;
    }

    public boolean isNameConflict(String name) {
        return false;
    }

    public String fetchNameConflict(String name) {
        return isNameConflict(name) ? (name + CharacterConstants.SEPARATOR_UNDERLINE + NamespaceConstants.pamirs) : name;
    }

}
