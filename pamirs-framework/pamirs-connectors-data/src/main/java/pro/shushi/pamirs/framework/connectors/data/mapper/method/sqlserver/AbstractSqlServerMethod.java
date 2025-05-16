package pro.shushi.pamirs.framework.connectors.data.mapper.method.sqlserver;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.NEWLINE;

/**
 * 通用 SQL Statement  生成方法抽象类
 * <p>
 * 2020/6/16 1:39 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AbstractSqlServerMethod {

    private final ModelConfig modelConfig;

    public AbstractSqlServerMethod(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    public ModelConfig getModelConfig() {
        return this.modelConfig;
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
            return script + sqlServerConvertIf("!" + judgeCondition);
//            return script + SqlScriptUtils.convertIf(optionSqlScript,"!" + judgeCondition,false);
        } else {
            return convertIf(sqlScript, prefix, modelFieldConfig, fieldStrategy);
        }
    }

    /**
     * <p>
     * 获取 带 if 标签的脚本
     * </p>
     * @return if 脚本
     */
    public static String sqlServerConvertIf(final String ifTest) {
        return String.format("<if test=\"%s\">%s</if>", ifTest, "DEFAULT , ");
    }


    //TODO:
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
