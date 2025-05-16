package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataEditApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Map;

/**
 * 编程式元数据编辑API
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
public class DefaultMetaDataEditor implements MetaDataEditApi {

    @Override
    public void edit(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap) {
        if (!command.getOptions().isEditMeta()) {
            return;
        }
        for (MetaDataEditor editor : BeanDefinitionUtils.getBeansOfTypeByOrdered(MetaDataEditor.class)) {
            long start = System.currentTimeMillis();
            editor.edit(command, metaMap);
            log.info("{} meta data editor cost time: {}ms", editor.getClass().getName(), System.currentTimeMillis() - start);
        }
    }

}
