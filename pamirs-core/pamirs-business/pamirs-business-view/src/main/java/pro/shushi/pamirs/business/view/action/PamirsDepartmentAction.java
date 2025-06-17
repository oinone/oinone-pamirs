package pro.shushi.pamirs.business.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.pmodel.DepartmentRelEmployeeProxy;
import pro.shushi.pamirs.business.api.service.PamirsDepartmentService;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.Comparator;
import java.util.List;

/**
 * @author xzf (xzf@shushi.pro)
 * @date 2023/1/9 12:06
 */
@Component
@Model.model(PamirsDepartment.MODEL_MODEL)
public class PamirsDepartmentAction {
    @Autowired
    private PamirsDepartmentService pamirsDepartmentService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsDepartment construct(PamirsDepartment data) {
        return data.construct();
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsDepartment constructMirror(PamirsDepartment data) {
        return data;
    }


    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "部门名称不允许为空"),
    })
    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public PamirsDepartment create(PamirsDepartment data) {
        data = pamirsDepartmentService.create(data);
        return data;
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "部门名称不允许为空"),
    })
    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public PamirsDepartment update(PamirsDepartment data) {
        pamirsDepartmentService.update(data);
        return data;
    }

    @Action.Advanced(name = FunctionConstant.delete, managed = true)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstant.delete)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<PamirsDepartment> delete(List<PamirsDepartment> list) {
        pamirsDepartmentService.deleteByPks(list);
        return list;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public PamirsDepartment deleteOne(PamirsDepartment data) {
        pamirsDepartmentService.deleteByPk(data);
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<PamirsDepartment> queryPage(Pagination<PamirsDepartment> page, IWrapper<PamirsDepartment> queryWrapper) {
        return pamirsDepartmentService.queryPage(page, queryWrapper);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public PamirsDepartment queryOne(PamirsDepartment query) {
        PamirsDepartment department = pamirsDepartmentService.queryOne(query);
        return department;
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "计算部门主管")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsDepartment constructEmployeeRel(PamirsDepartment data) {
        List<DepartmentRelEmployeeProxy> relList = data.getEmployeeRelList();
        if (CollectionUtils.isEmpty(relList)) {
            return data;
        }
        boolean supervisorFoundAfterFirst = false;
        for (int i = 1; i < relList.size(); i++) {
            DepartmentRelEmployeeProxy rel = relList.get(i);
            if (Boolean.TRUE.equals(rel.getSupervisor())) {
                if (!supervisorFoundAfterFirst) {
                    supervisorFoundAfterFirst = true;
                    relList.get(0).setSupervisor(false);
                }
            } else {
                rel.setSupervisor(false);
            }
        }
        relList.sort(Comparator.comparing((DepartmentRelEmployeeProxy r) -> Boolean.TRUE.equals(r.getSupervisor())).reversed());
        return data;
    }
}
