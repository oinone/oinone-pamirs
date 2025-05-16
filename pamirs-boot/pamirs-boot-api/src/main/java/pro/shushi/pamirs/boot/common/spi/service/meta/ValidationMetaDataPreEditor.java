package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataPreEditor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 在生命周期中预处理校验表达式和校验函数
 * <p>
 * 2021/3/2 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
public class ValidationMetaDataPreEditor implements MetaDataPreEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        Set<String> dealSet = new HashSet<>();
        for (String module : metaMap.keySet()) {
            Meta meta = metaMap.get(module);
            Map<String, MetaData> metaDataMap = meta.getData();
            for (String dependentModule : metaDataMap.keySet()) {
                if (dealSet.contains(dependentModule)) {
                    continue;
                }
                dealSet.add(dependentModule);
                MetaData metaData = metaDataMap.get(dependentModule);
                metaData.initLifecycleValidation();
            }
        }
    }

}
