package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.DsDialectComponent;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.Optional;

import static pro.shushi.pamirs.meta.constant.ModuleFunctionConstants.DataSourceFunction.BEAN_NAME;
import static pro.shushi.pamirs.meta.constant.ModuleFunctionConstants.DataSourceFunction.GET_DATABASE_FUN;

/**
 * 获取数据源远程服务
 *
 * @author Adamancy Zhang at 14:43 on 2025-06-12
 */
@Component(BEAN_NAME)
public class DataSourceFetcher {

    public String getDatabaseByModel(ModelConfig modelConfig) {
        String dsKey = Optional.ofNullable(modelConfig)
                .map(ModelConfig::getDsKey)
                .orElse(null);
        String module = Optional.ofNullable(modelConfig)
                .map(ModelConfig::getModule)
                .orElse(null);
        if (StringUtils.isBlank(module)) {
            return getDatabase(dsKey);
        }
        Function function = PamirsSession.getContext().getFunctionAllowNull(module, GET_DATABASE_FUN);
        if (function == null) {
            return getDatabase(dsKey);
        }
        return Fun.run(function, dsKey);
    }

    public String getDatabase(String dsKey) {
        return Dialects.component(DsDialectComponent.class, dsKey).getDatabase(dsKey);
    }
}
