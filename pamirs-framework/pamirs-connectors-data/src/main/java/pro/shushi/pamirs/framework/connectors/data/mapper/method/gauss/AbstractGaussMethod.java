package pro.shushi.pamirs.framework.connectors.data.mapper.method.gauss;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.plugin.util.FieldUtil;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Optional;

/**
 * 通用 SQL Statement 生成方法抽象类
 *
 * @author paidaxing
 * @version 1.0.0
 * @date 2024/03/14 14:10:58
 */
public class AbstractGaussMethod {
    private final ModelConfig modelConfig;

    public AbstractGaussMethod(ModelConfig modelConfig) {
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
            return script + SqlScriptUtils.convertIf("DEFAULT" + CharacterConstants.SEPARATOR_COMMA, "!" + judgeCondition, false);
        } else {
            return convertIf(sqlScript, prefix, modelFieldConfig, fieldStrategy);
        }
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

    protected boolean isAutoIncrementModelField(ModelConfig modelConfig, ModelFieldConfig modelFieldConfig) {
        if (modelFieldConfig.getPk()) {
            String fieldKeyGenerator = Optional.ofNullable(modelFieldConfig.getKeyGenerator())
                    .filter(s -> !s.equals(KeyGeneratorEnum.NON.value())).orElseGet(() -> {
                        PamirsTableInfo pamirsTableConfig = modelConfig.getPamirsTableInfo();
                        if (pamirsTableConfig == null) {
                            // 此处可能无法获取PamirsTableInfo对象，需要重新生成
                            pamirsTableConfig = PamirsTableInfo.fetchPamirsTableConfig(modelConfig.getModelDefinition());
                            modelConfig.setPamirsTableInfo(pamirsTableConfig);
                        }
                        return pamirsTableConfig.getKeyGenerator();
                    });
            return StringUtils.isNotBlank(fieldKeyGenerator) && fieldKeyGenerator.equals(KeyGeneratorEnum.AUTO_INCREMENT.value());
        }
        return false;
    }

}
