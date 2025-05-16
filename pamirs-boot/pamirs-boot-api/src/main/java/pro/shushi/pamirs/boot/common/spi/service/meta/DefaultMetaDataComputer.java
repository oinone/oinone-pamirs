package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.enmu.BootExpEnumerate;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataComputerApi;
import pro.shushi.pamirs.framework.compute.exception.MetaDataComputeException;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.MetaDataModelComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 元数据计算API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultMetaDataComputer implements MetaDataComputerApi {

    @Resource
    private MetaConfiguration metaConfiguration;

    @Override
    public void compute(AppLifecycleCommand command, ComputeContext context, List<Meta> metaList, Set<String> completedModuleSet) {
        if (!command.getOptions().isComputeMeta()) {
            return;
        }
        if (null == completedModuleSet) {
            completedModuleSet = new HashSet<>();
        }
        try {
            long start = System.currentTimeMillis();
            Spider.getDefaultExtension(MetaDataModelComputer.class).compute(context, metaList, completedModuleSet);
            log.info("完成计算所有元数据 {}ms", System.currentTimeMillis() - start);
        } catch (MetaDataComputeException e) {
            throw PamirsException.construct(BootExpEnumerate.BASE_BOOT_META_DATA_COMPUTE_ERROR, e).errThrow();
        }
    }
}
