package pro.shushi.pamirs.trigger.action;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.trigger.model.TriggerTaskAction;

/**
 * @author xzf 2022/06/30 12:08
 **/
@Base
@Component
@Model.model(TriggerTaskAction.MODEL_MODEL)
public class TriggerTaskActionAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.displayName)", error = "显示名称不允许为空"),
            @Validation.Rule(value = "!IS_BLANK(data.technicalName)", error = "technicalName不允许为空"),
            @Validation.Rule(value = "!IS_NULL(data.model)", error = "触发模型不允许为空"),
            @Validation.Rule(value = "!IS_NULL(data.condition)", error = "触发场景不允许为空"),
            @Validation.Rule(value = "!IS_NULL(data.active)", error = "是否启用不允许为空"),
    })
    @Action(displayName = "创建")
    public TriggerTaskAction create(TriggerTaskAction data) {
        return defaultWriteWithFieldApi.createWithField(data);
    }


    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.displayName)", error = "显示名称不允许为空"),
            @Validation.Rule(value = "!IS_BLANK(data.technicalName)", error = "technicalName不允许为空"),
            @Validation.Rule(value = "!IS_NULL(data.model)", error = "触发模型不允许为空"),
            @Validation.Rule(value = "!IS_NULL(data.condition)", error = "触发场景不允许为空"),
            @Validation.Rule(value = "!IS_NULL(data.active)", error = "是否启用不允许为空"),
    })
    @Action(displayName = "更新")
    public TriggerTaskAction update(TriggerTaskAction data) {
        return defaultWriteWithFieldApi.updateWithField(data);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<TriggerTaskAction> queryPage(Pagination<TriggerTaskAction> page, IWrapper<TriggerTaskAction> queryWrapper) {
        Pagination<TriggerTaskAction> resultPage = Models.origin().queryPage(page, queryWrapper);

        return resultPage;
    }

}
