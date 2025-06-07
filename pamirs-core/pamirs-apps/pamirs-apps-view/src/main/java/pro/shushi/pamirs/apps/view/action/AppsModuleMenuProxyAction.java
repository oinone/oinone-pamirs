package pro.shushi.pamirs.apps.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.pmodel.AppsModuleMenuProxy;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

/**
 * 前端调用树查询DB层ORM未去获取引用字段
 *
 * @author WuXin at 19:01 on 2024/12/12
 */
@Slf4j
@Base
@Component
@Model.model(AppsModuleMenuProxy.MODEL_MODEL)
public class AppsModuleMenuProxyAction {

    @Function.Advanced(displayName = "查询菜单", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AppsModuleMenuProxy> queryPage(Pagination<AppsModuleMenuProxy> page, IWrapper<AppsModuleMenuProxy> queryWrapper) {
        Pagination<AppsModuleMenuProxy> appsModuleMenuProxyPagination = new AppsModuleMenuProxy().queryPage(page, queryWrapper);
        List<AppsModuleMenuProxy> content = appsModuleMenuProxyPagination.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            content.forEach(item -> {
                        item.setDisplayName(item.getDisplayName());
                        item.setParentName(item.getParentName());
                    }
            );
        }
        return appsModuleMenuProxyPagination;
    }

    @Function.Advanced(displayName = "根据条件查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<AppsModuleMenuProxy> queryListByWrapper(IWrapper<AppsModuleMenuProxy> queryWrapper) {
        List<AppsModuleMenuProxy> appsModuleMenuProxies = new AppsModuleMenuProxy().queryList(queryWrapper);
        if (CollectionUtils.isNotEmpty(appsModuleMenuProxies)) {
            appsModuleMenuProxies.forEach(item -> {
                item.setDisplayName(item.getDisplayName());
                item.setParentName(item.getParentName());
            });
        }
        return appsModuleMenuProxies;
    }
}
