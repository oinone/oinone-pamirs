package pro.shushi.pamirs.business.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.entity.PamirsPerson;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.service.entity.PamirsPersonService;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.List;

/**
 * {@link PamirsPerson}动作
 *
 * @author Adamancy Zhang at 12:27 on 2021-08-31
 */
@Component
@Model.model(PamirsPerson.MODEL_MODEL)
public class PamirsPersonAction {

    @Autowired
    private PamirsPersonService pamirsPersonService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsPerson construct(PamirsPerson data) {
        return data.construct();
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsPerson constructMirror(PamirsPerson data) {
        return data;
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public PamirsPerson create(PamirsPerson data) {
        data.setPartnerType(BusinessPartnerTypeEnum.PERSON);
        return pamirsPersonService.create(data);
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public PamirsPerson update(PamirsPerson data) {
        return pamirsPersonService.update(data);
    }

    @Action.Advanced(name = FunctionConstant.delete, managed = true)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstant.delete)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<PamirsPerson> delete(List<PamirsPerson> list) {
        pamirsPersonService.delete(list);
        return list;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public PamirsPerson deleteOne(PamirsPerson data) {
        pamirsPersonService.deleteOne(data);
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<PamirsPerson> queryPage(Pagination<PamirsPerson> page, IWrapper<PamirsPerson> queryWrapper) {
        return pamirsPersonService.queryPage(page, WrapperHelper.lambda(queryWrapper));
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public PamirsPerson queryOne(PamirsPerson query) {
        PamirsPerson pamirsPerson = pamirsPersonService.queryOne(query);
        return pamirsPerson;
    }

}
