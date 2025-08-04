package pro.shushi.pamirs.boot.web.spi.meta;

import org.apache.commons.collections4.MapUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.boot.web.init.ViewLayoutInitLoader;
import pro.shushi.pamirs.boot.web.init.ViewMaskInitLoader;
import pro.shushi.pamirs.boot.web.init.ViewTemplateInitLoader;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 导入XML定义视图元数据编辑计算
 * <p>
 * 在加载首页和菜单元数据、生成默认窗口动作之后执行
 * 2022/3/1 3:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 77)
@Component
public class LoadViewEditor implements MetaDataEditor {

    @Resource
    private ViewTemplateInitLoader viewTemplateInitLoader;

    @Resource
    private ViewLayoutInitLoader viewLayoutInitLoader;

    @Resource
    private ViewMaskInitLoader viewMaskInitLoader;

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        if (MapUtils.isNotEmpty(metaMap)) {
            for (String module : metaMap.keySet()) {
                Meta meta = metaMap.get(module);
                MetaData metaData = meta.getCurrentModuleData();

                // 加载jar包中的视图
                viewTemplateInitLoader.init(module, metaData);
                viewLayoutInitLoader.init(module, metaData);
                viewMaskInitLoader.init(module, metaData);

            }
        }

    }
}
