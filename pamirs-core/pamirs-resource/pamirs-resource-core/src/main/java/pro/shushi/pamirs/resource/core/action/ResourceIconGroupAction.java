package pro.shushi.pamirs.resource.core.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;
import pro.shushi.pamirs.resource.api.service.ResourceIconGroupService;

import java.util.List;

@Component
@Model.model(ResourceIconGroup.MODEL_MODEL)
public class ResourceIconGroupAction {

    @Autowired
    private ResourceIconGroupService resourceIconGroupService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "查询全部分组", managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<ResourceIconGroup> queryAllGroupData() {
        return resourceIconGroupService.queryAllGroupData();
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "查询全部分组", managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public List<ResourceIconGroup> queryGroupData() {
        return resourceIconGroupService.queryGroupData();
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "创建分组", summary = "创建分组", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public ResourceIconGroup create(ResourceIconGroup data) {
        data = resourceIconGroupService.create(data);
        return data;
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "修改分组", summary = "修改分组", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public ResourceIconGroup update(ResourceIconGroup data) {
        resourceIconGroupService.update(data);
        return data;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public ResourceIconGroup deleteOne(ResourceIconGroup data) {
        resourceIconGroupService.deleteOne(data);
        return data;
    }
}
