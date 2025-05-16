package pro.shushi.pamirs.business.view.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.service.entity.PamirsCompanyService;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.core.common.function.FunctionConstant;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author xzf 2023/1/9 12:07
 */
@Component
@Model.model(PamirsCompany.MODEL_MODEL)
public class PamirsCompanyAction {

    @Autowired
    private PamirsCompanyService pamirsCompanyService;

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsCompany construct(PamirsCompany data) {
        return data.construct();
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "部门下拉触发")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public PamirsCompany constructMirror(PamirsCompany data) {
        return data;
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "公司名称不允许为空"),
    })
    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public PamirsCompany create(PamirsCompany data) {
        data.setPartnerType(BusinessPartnerTypeEnum.COMPANY);
        data = pamirsCompanyService.create(data);
        return data;
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "公司名称不允许为空"),
    })
    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public PamirsCompany update(PamirsCompany data) {
        pamirsCompanyService.updateById(data);

        updateAppConfigLogo(data.getCode(), data.getLogoUrl());
        return data;
    }

    private void updateAppConfigLogo(String companyCode, String logo) {
        AppConfig appConfig = new AppConfig();
        appConfig.setCode(AppConfig.generateCode(AppConfigScopeEnum.GLOBAL));
        appConfig.setCompanyCode(companyCode);
        appConfig.setScope(AppConfigScopeEnum.GLOBAL);
        appConfig = appConfig.queryOne();
        if (appConfig == null) {
            return;
        }
        appConfig.setLogo(logo);
        appConfig.updateById();
    }

    @Action.Advanced(name = FunctionConstant.delete, managed = true)
    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstant.delete)
    @Function.fun(FunctionConstant.deleteWithFieldBatch)
    public List<PamirsCompany> delete(List<PamirsCompany> list) {
        List<Long> companyIds = ListUtils.transform(list, PamirsCompany::getId);
        Map<Long,PamirsCompany> companyMap = FetchUtil.fetchMapByIds(PamirsCompany.class, companyIds);
        list.forEach(company->{
            PamirsCompany dbCompany = companyMap.get(company.getId());
            if (dbCompany!=null && StringUtils.isNotBlank(dbCompany.getCode())) {
                // 已存在子公司的公司不允许删除
                Long count = new PamirsCompany().count(Pops.<PamirsCompany>lambdaQuery()
                        .from(PamirsCompany.MODEL_MODEL).eq(PamirsCompany::getParentCode, dbCompany.getCode()));
                if (count > 0) {
                    throw PamirsException.construct(BusinessExpEnumerate.COMPANY_HAS_CHILD_DELETE_ERROR).errThrow();
                }
            }
        });

        pamirsCompanyService.deleteList(list);
        return list;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE)
    public PamirsCompany deleteOne(PamirsCompany data) {
        PamirsCompany dbCompany = data.queryById();
        if (dbCompany!=null && StringUtils.isNotBlank(dbCompany.getCode())) {
            // 已存在子公司的公司不允许删除
            Long count = new PamirsCompany().count(Pops.<PamirsCompany>lambdaQuery()
                    .from(PamirsCompany.MODEL_MODEL).eq(PamirsCompany::getParentCode, dbCompany.getCode()));
            if (count > 0) {
                throw PamirsException.construct(BusinessExpEnumerate.COMPANY_HAS_CHILD_DELETE_ERROR).errThrow();
            }
        }

        pamirsCompanyService.deleteOne(data);
        return data;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<PamirsCompany> queryPage(Pagination<PamirsCompany> page, IWrapper<PamirsCompany> queryWrapper) {
        return pamirsCompanyService.queryPage(page, WrapperHelper.lambda(queryWrapper));
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public PamirsCompany queryOne(PamirsCompany query) {
        PamirsCompany pamirsCompany = pamirsCompanyService.queryOne(query);
        pamirsCompany = pamirsCompany.fieldQuery(PamirsCompany::getParent);
//        pamirsCompany = pamirsCompany.fieldQuery(PamirsCompany::getDepartmentList);
        pamirsCompany = pamirsCompany.fieldQuery(PamirsCompany::getEmployeeList);
        return pamirsCompany;
    }

}
