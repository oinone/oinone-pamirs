package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.TransactionConfig;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import java.lang.reflect.Method;

/**
 * 事务签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@SPI.Service(TransactionConfig.MODEL_MODEL)
public class TransactionReflectSigner implements ModelReflectSigner<TransactionConfig, Method> {

    @Override
    public String sign(MetaNames names, Method source) {
        String namespace = NamespaceAndFunUtils.namespace(source);
        String fun = NamespaceAndFunUtils.fun(source);
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(fun)) {
            return null;
        }
        return FunctionDefinition.sign(namespace, fun);
    }

}
