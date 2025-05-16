package pro.shushi.pamirs.framework.connectors.data.sql.config;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkSystemConfiguration;

import java.util.Optional;

/**
 * 模型字段持久层包装类
 * <p>
 * 2020/6/16 2:01 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ModelFieldConfigWrapper {

    private ModelFieldConfig modelFieldConfig;

    public static ModelFieldConfigWrapper wrap(ModelFieldConfig modelFieldConfig) {
        return new ModelFieldConfigWrapper().setModelFieldConfig(modelFieldConfig);
    }

    public String getSqlSelect() {
        return getSqlSelect(Boolean.TRUE);
    }

    public String getSqlSelect(boolean onlyColumn) {
        String column = getColumn();
        String asProperty = this.getModelFieldConfig().getLname();
        if (onlyColumn && this.getModelFieldConfig().getOnlyColumn()) {
            return column;
        } else if (StringUtils.isNotBlank(asProperty)) {
            String systemDsKey = CommonApiFactory.getApi(PamirsFrameworkSystemConfiguration.class).getOriginSystemDsKey();
            String propertyFormat = Optional.ofNullable(PamirsTableInfo.fetchGlobalTableConfig(systemDsKey)).map(PamirsTableInfo::getAliasFormat).orElse(null);
            if (StringUtils.isNotBlank(propertyFormat)) {
                asProperty = String.format(propertyFormat, asProperty);
            }
            column += (" AS " + asProperty);
        }
        return column;
    }

    public String getColumn() {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(this.getModelFieldConfig().getModel());
        String columnFormat = pamirsTableInfo.getColumnFormat();
        if (StringUtils.isNotBlank(columnFormat)) {
            return String.format(columnFormat, this.getModelFieldConfig().getColumn());
        }
        return this.getModelFieldConfig().getColumn();
    }

}
