package pro.shushi.pamirs.framework.connectors.data.plugin.sequence;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.mapping.MappedStatement;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.List;

/**
 * 获取主键列表
 * <p>
 * 2020/7/1 4:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class KeyPropertiesGetter {

    public static String[] get(MappedStatement ms, Object parameter) {
        String[] keyProperties = ms.getKeyProperties();
        if (ArrayUtils.isNotEmpty(keyProperties)) {
            if (CharacterConstants.SEPARATOR_ASTERISK.equals(keyProperties[0])) {
                String model = MapperContext.model(parameter);
                ModelConfig modelConfig = PamirsSession.getContext().getSimpleModelConfig(model);
                if (null == modelConfig || !modelConfig.havePk()) {
                    return null;
                }
                List<String> pkList = modelConfig.getPkProperties();
                String[] pks = new String[pkList.size()];
                return pkList.toArray(pks);
            }
        }
        return keyProperties;
    }
}
