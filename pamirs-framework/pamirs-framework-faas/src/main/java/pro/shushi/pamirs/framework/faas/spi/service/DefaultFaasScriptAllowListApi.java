package pro.shushi.pamirs.framework.faas.spi.service;

import org.apache.commons.collections4.SetUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.faas.fun.builtin.*;
import pro.shushi.pamirs.framework.faas.spi.api.guard.FaasScriptAllowListApi;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 支持表达式调用的函数白名单与黑名单SPI默认实现
 * <p>
 * 2021/3/4 11:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultFaasScriptAllowListApi implements FaasScriptAllowListApi {

    public static final Set<String> DEFAULT_SET = SetUtils.hashSet(

            CollectionFunctions.class.getName(),
            ContextFunctions.class.getName(),
            DateFunctions.class.getName(),
            LogicFunctions.class.getName(),
            MapFunctions.class.getName(),
            MathFunctions.class.getName(),
            ObjectFunctions.class.getName(),
            RegexFunctions.class.getName(),
            TextFunctions.class.getName()

    );

    public Set<String> classWhiteList() {
        return DEFAULT_SET;
    }

    public Set<String> namespaceWhiteList() {
        return SetUtils.hashSet(NamespaceConstants.expression);
    }

}
