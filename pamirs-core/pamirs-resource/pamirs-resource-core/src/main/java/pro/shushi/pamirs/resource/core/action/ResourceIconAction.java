package pro.shushi.pamirs.resource.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.resource.api.model.ResourceIcon;
import pro.shushi.pamirs.resource.api.service.ResourceIconService;

import java.util.List;

@Component
@Model.model(ResourceIcon.MODEL_MODEL)
public class ResourceIconAction {

    @Autowired
    private ResourceIconService resourceIconService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "按照匹配条件查询所有图标", managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<ResourceIcon> queryPage(Pagination<ResourceIcon> page, IWrapper<ResourceIcon> queryWrapper) {
        page.orderBy(SortDirectionEnum.DESC, ResourceIcon::getWriteDate)
                .orderBy(SortDirectionEnum.ASC, ResourceIcon::getName)
                .orderBy(SortDirectionEnum.ASC, ResourceIcon::getFullFontClass);
        Pagination<ResourceIcon> resourceIconPagination = resourceIconService.queryPage(page, queryWrapper);
        List<ResourceIcon> content = resourceIconPagination.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            new ResourceIcon().listFieldQuery(content, ResourceIcon::getLib);
            content.forEach(item -> item.setFullFontClass(item.getLib().getFontClassPrefix() + item.getFontClass()));
        }
        return resourceIconPagination;
    }

    @Action.Advanced(name = FunctionConstants.update, type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "修改图标", summary = "修改图标", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public ResourceIcon update(ResourceIcon data) {
        resourceIconService.update(data);
        return data;
    }

    @Action(displayName = "生效")
    public ResourceIcon active(ResourceIcon data) {
        resourceIconService.active(data);
        return data;
    }

    @Action(displayName = "失效")
    public ResourceIcon disabled(ResourceIcon data) {
        resourceIconService.disabled(data);
        return data;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public ResourceIcon deleteOne(ResourceIcon data) {
        resourceIconService.deleteOne(data);
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "按照fullFontClass查询所有图标")
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public ResourceIcon queryIconWithFullFontClass(String fullFontClass) {
        ResourceIcon data = resourceIconService.queryIcon(fullFontClass);
        return data;
    }
}
