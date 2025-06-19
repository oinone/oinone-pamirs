package pro.shushi.pamirs.boot.web.spi.meta;

import org.apache.commons.collections4.MapUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.boot.web.utils.UiActionUtils;
import pro.shushi.pamirs.boot.web.utils.ViewActionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Map;

/**
 * 默认窗口动作元数据编辑计算
 * <p>
 * 在加载首页和菜单元数据之后，生成默认视图之前执行
 * 2022/3/1 3:29 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(99)
@Component
public class DefaultViewActionEditor implements MetaDataEditor {

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        if (MapUtils.isNotEmpty(metaMap)) {
            for (String module : metaMap.keySet()) {
                UiActionUtils.doSomethingForMenuModel(metaMap, module, this::makeDefaultModelViewAction);
            }
        }
    }

    public void makeDefaultModelViewAction(Meta meta, ModelDefinition data) {

        // 创建 跳转新增页 viewAction
        ViewActionUtils.makeDefaultViewAction(meta, data,
                ViewActionConstants.redirectCreatePage.name,
                ViewActionConstants.redirectCreatePage.displayName,
                null,
                ActionContextTypeEnum.CONTEXT_FREE,
                ViewTypeEnum.FORM, ViewActionConstants.redirectCreatePage.priority);

        // 创建 跳转更新页 viewAction
        ViewActionUtils.makeDefaultViewAction(meta, data,
                ViewActionConstants.redirectUpdatePage.name,
                ViewActionConstants.redirectUpdatePage.displayName,
                null,
                ActionContextTypeEnum.SINGLE,
                ViewTypeEnum.FORM, ViewActionConstants.redirectUpdatePage.priority);

        // 创建 跳转详情页 viewAction
        ViewActionUtils.makeDefaultViewAction(meta, data,
                ViewActionConstants.redirectDetailPage.name,
                ViewActionConstants.redirectDetailPage.displayName,
                null,
                ActionContextTypeEnum.SINGLE,
                ViewTypeEnum.DETAIL, ViewActionConstants.redirectDetailPage.priority);

    }

}
