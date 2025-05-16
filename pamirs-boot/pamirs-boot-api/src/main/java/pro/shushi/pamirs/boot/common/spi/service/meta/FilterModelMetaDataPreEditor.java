package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataPreEditor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.configure.PamirsFrameworkMetaConfiguration;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 编辑元数据，过滤掉指定签名的元数据
 * <p>
 * 2021/3/2 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@Component
public class FilterModelMetaDataPreEditor implements MetaDataPreEditor {

    @Resource
    private PamirsFrameworkMetaConfiguration pamirsFrameworkMetaConfiguration;

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        Map<String/*module*/, Map<String/*model*/, List<String>/*sign*/>> filterMap =
                pamirsFrameworkMetaConfiguration.getFilter();
        if (MapUtils.isEmpty(filterMap)) {
            return;
        }
        for (String module : filterMap.keySet()) {
            Map<String/*model*/, List<String>/*sign*/> subFilterMap = filterMap.get(module);
            if (MapUtils.isEmpty(subFilterMap)) {
                continue;
            }
            Meta meta = metaMap.get(module);
            if (null == meta) {
                continue;
            }
            for (String model : subFilterMap.keySet()) {
                List<String> signs = subFilterMap.get(model);
                if (CollectionUtils.isEmpty(signs)) {
                    continue;
                }
                for (String sign : signs) {
                    meta.getCurrentModuleData().removeDataItem(model, sign);
                }
            }
        }
    }

}
