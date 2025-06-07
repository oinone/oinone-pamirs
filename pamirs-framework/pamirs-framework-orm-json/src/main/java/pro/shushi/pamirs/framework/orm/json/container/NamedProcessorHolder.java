package pro.shushi.pamirs.framework.orm.json.container;

import pro.shushi.pamirs.framework.orm.named.LnameToNameProcessor;
import pro.shushi.pamirs.framework.orm.named.NameToLnameProcessor;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

/**
 * 数据模板容器
 * <p>
 * 2021/9/23 10:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class NamedProcessorHolder {

    private final static HoldKeeper<LnameToNameProcessor> lnameToNameProcessorHolder = new HoldKeeper<>();

    private final static HoldKeeper<NameToLnameProcessor> nameToLnameProcessorHolder = new HoldKeeper<>();

    public static LnameToNameProcessor getLnameToNameProcessor() {
        return lnameToNameProcessorHolder.supply(() -> BeanDefinitionUtils.getBean(LnameToNameProcessor.class));
    }

    public static NameToLnameProcessor getNameToLnameProcessor() {
        return nameToLnameProcessorHolder.supply(() -> BeanDefinitionUtils.getBean(NameToLnameProcessor.class));
    }

}
