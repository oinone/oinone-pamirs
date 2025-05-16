package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.util.ExtNamespaceAndNameUtils;

import java.lang.reflect.Method;

/**
 * 扩展点签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(ExtPoint.MODEL_MODEL)
public class ExtPointReflectSigner implements ModelReflectSigner<ExtPoint, Method> {

    @Override
    public String sign(MetaNames names, Method source) {
        String namespace = ExtNamespaceAndNameUtils.namespace(source);
        String name = ExtNamespaceAndNameUtils.name(source);
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(name)) {
            return null;
        }
        return namespace + CharacterConstants.SEPARATOR_DOT + name;
    }

}
