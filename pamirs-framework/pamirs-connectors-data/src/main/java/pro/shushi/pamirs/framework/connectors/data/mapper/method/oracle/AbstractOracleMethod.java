package pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.AbstractMysqlMethod;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.FieldStrategyEnum;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.util.Optional;

/**
 * @author Adamancy Zhang at 12:08 on 2023-06-26
 */
public abstract class AbstractOracleMethod extends AbstractMysqlMethod {
    public AbstractOracleMethod(ModelConfig modelConfig) {
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
    @Override
    public String convertIf(final String sqlScript, final String optionSqlScript, String prefix, final ModelFieldConfig modelFieldConfig, final String fieldStrategy) {
        if (FieldStrategyEnum.NOT_CHANGE.value().equals(fieldStrategy)) {
            String judgeCondition = judgeCondition(prefix, modelFieldConfig);
            String script = SqlScriptUtils.convertIf(sqlScript, judgeCondition, false);
            return script + SqlScriptUtils.convertIf("DEFAULT" + CharacterConstants.SEPARATOR_COMMA, "!" + judgeCondition, false);
        } else {
            return convertIf(sqlScript, prefix, modelFieldConfig, fieldStrategy);
        }
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
