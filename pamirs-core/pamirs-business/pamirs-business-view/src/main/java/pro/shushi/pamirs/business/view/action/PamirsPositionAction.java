package pro.shushi.pamirs.business.view.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.business.api.model.PositionRelEmployee;
import pro.shushi.pamirs.business.api.service.PamirsPositionService;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
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
 * @author xzf (xzf@shushi.pro)
 * @date 2023/1/9 12:31
 */
@Component
@Model.model(PamirsPosition.MODEL_MODEL)
public class PamirsPositionAction {


    @Autowired
    private PamirsPositionService pamirsPositionService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsPosition construct(PamirsPosition data) {
        return data.construct();
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsPosition constructMirror(PamirsPosition data) {
        return data;
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public PamirsPosition create(PamirsPosition data) {
        return pamirsPositionService.create(data);
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public PamirsPosition update(PamirsPosition data) {
        pamirsPositionService.updateByPk(data);
        return data;
    }

    @Action.Advanced(name = FunctionConstant.delete, managed = true)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstant.delete)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<PamirsPosition> delete(List<PamirsPosition> list) {
        pamirsPositionService.deleteByPks(list);
        return list;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public PamirsPosition deleteOne(PamirsPosition data) {
        pamirsPositionService.deleteByPk(data);
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<PamirsPosition> queryPage(Pagination<PamirsPosition> page, IWrapper<PamirsPosition> queryWrapper) {
        return pamirsPositionService.queryPage(page, queryWrapper);
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public PamirsPosition queryOne(PamirsPosition query) {
        PamirsPosition pamirsPosition = pamirsPositionService.queryOne(query);
        pamirsPosition = pamirsPosition.fieldQuery(PamirsPosition::getDepartment);
        pamirsPosition = pamirsPosition.fieldQuery(PamirsPosition::getParent);
        pamirsPosition.setEmployeeCount(queryEmployeeCount(pamirsPosition));
        return pamirsPosition;
    }


    private Long queryEmployeeCount(PamirsPosition position) {
        return new PositionRelEmployee().setPositionId(position.getId()).count(Pops.<PositionRelEmployee>lambdaQuery().from(PositionRelEmployee.MODEL_MODEL).eq(PositionRelEmployee::getPositionId, position.getId()));
    }
}
