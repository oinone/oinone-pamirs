package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsMapperConfigurationProxy;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.PamirsPersistenceConfigurationProxy;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.io.Serializable;
import java.util.Optional;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_MODEL_CONFIG_IS_NOT_EXISTS_ERROR;

/**
 * 表配置信息
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 11:05 上午
 */
@Data
public class PamirsTableInfo implements Serializable {

    private static final long serialVersionUID = -4798504378865110165L;

    private Boolean logicDelete;

    private String logicDeleteColumn;

    private String logicDeleteValue;

    private String logicNotDeleteValue;

    private String keyGenerator;

    private Boolean underCamel;

    private Boolean capitalMode;

    private String columnFormat;

    private String tableFormat;

    private String aliasFormat;

    private String charset;

    private String collate;

    public static PamirsTableInfo fetchPamirsTableInfo(ModelDefinition modelDefinition) {
        return fetchPamirsTableConfig(modelDefinition);
    }

    public static PamirsTableConfig fetchPamirsTableConfig(ModelDefinition modelDefinition) {
        String dsKey = Optional.ofNullable(modelDefinition).map(ModelDefinition::getCompletedDsKey).orElse(null);
        PamirsTableConfig yamlPamirsTableInfo = Optional.ofNullable(CommonApiFactory.getApi(PamirsMapperConfigurationProxy.class))
                .map(v -> v.fetchPamirsTableConfig(dsKey)).orElse(new PamirsTableConfig().defaultValue(dsKey));
        PamirsTableInfo modelPamirsTableInfo = fillPamirsTableInfo(modelDefinition);
        return Optional.ofNullable(mergePamirsTableInfo(yamlPamirsTableInfo, modelPamirsTableInfo)).orElse(new PamirsTableConfig().defaultValue(dsKey));
    }

    public static PamirsTableConfig fetchPamirsTableInfo(String model) {
        return fetchPamirsTableConfig(model);
    }

    public static PamirsTableConfig fetchPamirsTableConfig(String model) {
        ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
        String dsKey = Optional.ofNullable(modelConfig).map(ModelConfig::getDsKey).orElse(null);
        PamirsTableConfig yamlPamirsTableConfig = Optional.ofNullable(CommonApiFactory.getApi(PamirsMapperConfigurationProxy.class))
                .map(v -> v.fetchPamirsTableConfig(dsKey)).orElse(new PamirsTableConfig().defaultValue(dsKey));
        PamirsTableInfo modelPamirsTableInfo;
        if (null == modelConfig) {
            throw PamirsException.construct(BASE_MODEL_CONFIG_IS_NOT_EXISTS_ERROR).appendMsg("model:" + model).errThrow();
        }
        if (null != modelConfig.getPamirsTableInfo()) {
            modelPamirsTableInfo = modelConfig.getPamirsTableInfo();
        } else {
            modelPamirsTableInfo = fillPamirsTableInfo(modelConfig.getModelDefinition());
            modelConfig.setPamirsTableInfo(modelPamirsTableInfo);
        }
        return Optional.ofNullable(mergePamirsTableInfo(yamlPamirsTableConfig, modelPamirsTableInfo)).orElse(new PamirsTableConfig().defaultValue(dsKey));
    }

    public static PamirsTableConfig fetchGlobalTableConfig(String dsKey) {
        return Optional.ofNullable(CommonApiFactory.getApi(PamirsMapperConfigurationProxy.class))
                .map(v -> v.fetchPamirsTableConfig(dsKey)).orElse(null);
    }

    public static PamirsTableConfig mergePamirsTableInfo(PamirsTableConfig config, PamirsTableInfo info) {
        if (null == config && null == info) {
            return null;
        } else if (null == config) {
            return info.generateConfig();
        } else if (null == info) {
            return config;
        }
        if (null != info.getLogicDelete()) {
            config.setLogicDelete(info.getLogicDelete());
        }
        if (null != info.getLogicDeleteColumn()) {
            config.setLogicDeleteColumn(info.getLogicDeleteColumn());
        }
        if (null != info.getLogicDeleteValue()) {
            config.setLogicDeleteValue(info.getLogicDeleteValue());
        }
        if (null != info.getLogicNotDeleteValue()) {
            config.setLogicNotDeleteValue(info.getLogicNotDeleteValue());
        }
        if (null != info.getKeyGenerator()) {
            config.setKeyGenerator(info.getKeyGenerator());
        }
        if (null != info.getUnderCamel()) {
            config.setUnderCamel(info.getUnderCamel());
        }
        if (null != info.getCapitalMode()) {
            config.setCapitalMode(info.getCapitalMode());
        }
        if (null != info.getColumnFormat()) {
            config.setColumnFormat(info.getColumnFormat());
        }
        if (null != info.getTableFormat()) {
            config.setTableFormat(info.getTableFormat());
        }
        if (null != info.getAliasFormat()) {
            config.setAliasFormat(info.getAliasFormat());
        }
        if (null != info.getCharset()) {
            config.setCharset(info.getCharset());
        }
        if (null != info.getCollate()) {
            config.setCollate(info.getCollate());
        }
        return config;
    }

    public static PamirsTableInfo fillPamirsTableInfo(ModelDefinition modelDefinition) {
        if (null == modelDefinition) {
            return null;
        }
        PamirsTableInfo pamirsTableInfo = new PamirsTableInfo();
        int count = 0;
        if (null != modelDefinition.getLogicDelete()) {
            pamirsTableInfo.setLogicDelete(modelDefinition.getLogicDelete());
            count++;
        }
        if (null != modelDefinition.getLogicDeleteColumn()) {
            pamirsTableInfo.setLogicDeleteColumn(modelDefinition.getLogicDeleteColumn());
            count++;
        }
        if (null != modelDefinition.getLogicDeleteValue()) {
            pamirsTableInfo.setLogicDeleteValue(modelDefinition.getLogicDeleteValue());
            count++;
        }
        if (null != modelDefinition.getLogicNotDeleteValue()) {
            pamirsTableInfo.setLogicNotDeleteValue(modelDefinition.getLogicNotDeleteValue());
            count++;
        }
        if (!CollectionUtils.isEmpty(modelDefinition.getModelFields())) {
            KeyGeneratorEnum keyGenerator = null;
            if (isSharding(modelDefinition.getModule(), modelDefinition.getModel())) {
                keyGenerator = KeyGeneratorEnum.DISTRIBUTION;
            } else {
                for (ModelField modelField : modelDefinition.getModelFields()) {
                    if (null != modelField.getPk() && modelField.getPk()
                            && null != modelField.getKeyGenerator() && !KeyGeneratorEnum.NON.equals(modelField.getKeyGenerator())) {
                        keyGenerator = modelField.getKeyGenerator();
                    }
                }
            }
            if (null != keyGenerator) {
                pamirsTableInfo.setKeyGenerator(keyGenerator.value());
                count++;
            }
        }
        if (null != modelDefinition.getUnderCamel()) {
            pamirsTableInfo.setUnderCamel(modelDefinition.getUnderCamel());
            count++;
        }
        if (null != modelDefinition.getCapitalMode()) {
            pamirsTableInfo.setCapitalMode(modelDefinition.getCapitalMode());
            count++;
        }
        if (null != modelDefinition.getCharset()) {
            pamirsTableInfo.setCharset(modelDefinition.getCharset().value());
            count++;
        }
        if (null != modelDefinition.getCollate()) {
            pamirsTableInfo.setCollate(modelDefinition.getCollate().value());
            count++;
        }
        if (0 == count) {
            return null;
        } else {
            return pamirsTableInfo;
        }
    }

    public PamirsTableConfig generateConfig() {
        return (PamirsTableConfig) new PamirsTableConfig()
                .setLogicDelete(logicDelete)
                .setLogicDeleteColumn(logicDeleteColumn)
                .setLogicDeleteValue(logicDeleteValue)
                .setLogicNotDeleteValue(logicNotDeleteValue)
                .setKeyGenerator(keyGenerator)
                .setCapitalMode(capitalMode)
                .setUnderCamel(underCamel)
                .setColumnFormat(columnFormat)
                .setTableFormat(tableFormat)
                .setAliasFormat(aliasFormat)
                .setCharset(charset)
                .setCollate(collate);
    }

    public PamirsTableInfo defaultValue(String dsKey) {
        PamirsMapperConfigurationProxy pamirsMapperConfigurationProxy = CommonApiFactory.getApi(PamirsMapperConfigurationProxy.class);
        pamirsMapperConfigurationProxy.fillDefaultConfig(dsKey, this);
        return this;
    }

    public static String fetchKeyGenerator(String model) {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(model);
        if (null == pamirsTableInfo) {
            return null;
        }
        return pamirsTableInfo.getKeyGenerator();
    }

    public static boolean isAutoIncrement(String keyGenerator) {
        return StringUtils.isBlank(keyGenerator)
                || keyGenerator.equals(KeyGeneratorEnum.NON.value())
                || keyGenerator.equals(KeyGeneratorEnum.AUTO_INCREMENT.value());
    }

    public static boolean isAutoIncrementModel(String model) {
        String keyGenerator = fetchKeyGenerator(model);
        return isAutoIncrement(keyGenerator);
    }

    private static boolean isSharding(String module, String model) {
        PamirsPersistenceConfigurationProxy pamirsPersistenceConfigurationProxy = CommonApiFactory.getApi(PamirsPersistenceConfigurationProxy.class);
        if (null == pamirsPersistenceConfigurationProxy) {
            return false;
        }
        return pamirsPersistenceConfigurationProxy.isSharding(module, model);
    }

}
