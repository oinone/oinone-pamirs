package pro.shushi.pamirs.boot.common.spi.api.infrastructure;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.ExtendAfterBuilderInit;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.*;

/**
 * 启动扩展构建后置接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExtendAfterBuilderApi {

    default void build(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap) {
        Map<String, ExtendAfterBuilderInit> initMap = BeanDefinitionUtils.getBeansOfType(ExtendAfterBuilderInit.class);
        List<ExtendAfterBuilderInit> initList = new ArrayList<>(Objects.requireNonNull(initMap).values());
        initList.sort(Comparator.comparingInt(Prioritized::priority));
        // 系统初始化数据后置处理
        for (ExtendAfterBuilderInit initBuilder : initList) {
            initBuilder.init(command, metaMap);
        }
    }

}
