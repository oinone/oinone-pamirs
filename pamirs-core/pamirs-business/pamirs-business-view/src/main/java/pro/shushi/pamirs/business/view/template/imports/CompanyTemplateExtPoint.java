package pro.shushi.pamirs.business.view.template.imports;

import com.alibaba.excel.exception.ExcelAnalysisException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.enmu.AppConfigScopeEnum;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.enumeration.BusinessPartnerTypeEnum;
import pro.shushi.pamirs.business.api.service.entity.PamirsCompanyService;
import pro.shushi.pamirs.business.view.template.PamirsCompanyTemplate;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Wuxin
 * @Date 2024/7/22
 * @since 1.0
 */
@Slf4j
@Component
@Ext(ExcelImportTask.class)
public class CompanyTemplateExtPoint extends AbstractExcelImportDataExtPointImpl<PamirsCompany> {

    @Autowired
    private PamirsCompanyService pamirsCompanyService;

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.name==\"" + PamirsCompanyTemplate.TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, PamirsCompany data) {
        //校验数据
        dataValidator(data);
        //填充默认值
        defaultValueFiller(data);
        calcExcel(data);
        return true;
    }

    private void calcExcel(PamirsCompany data) {
        LambdaQueryWrapper<PamirsCompany> queryWrapper = Pops.<PamirsCompany>lambdaQuery()
                .from(PamirsCompany.MODEL_MODEL).eq(PamirsCompany::getCode, data.getCode());
        Long count = new PamirsCompany().count(queryWrapper);
        if (count > 0) {
            PamirsCompany pamirsCompany = new PamirsCompany().queryOneByWrapper(queryWrapper);
            pamirsCompany.setName(data.getName());
            pamirsCompany.setDataStatus(data.getDataStatus());
            pamirsCompany.setParent(data.getParent());
            String parentCode = Optional.ofNullable(data.getParent()).map(PamirsCompany::getCode).orElse(null);
            pamirsCompany.setParentCode(parentCode);
            pamirsCompany.setRemark(data.getRemark());
            pamirsCompanyService.updateById(pamirsCompany);
            updateAppConfigLogo(data.getCode(), data.getLogoUrl());
        } else {
            pamirsCompanyService.create(data);
        }
    }

    private void defaultValueFiller(PamirsCompany data) {
        if (data.getDataStatus() == null) {
            data.setDataStatus(DataStatusEnum.ENABLED);
        }
        data.setPartnerType(BusinessPartnerTypeEnum.COMPANY);
    }

    private void dataValidator(PamirsCompany data) {
        Predicate<String> isBlank = StringUtils::isBlank;
        if (isBlank.test(data.getCode())) {
            throw new ExcelAnalysisException(BusinessExpEnumerate.COMPANY_CODE_EMPTY.msg());
        }
        if (isBlank.test(data.getName())) {
            throw new ExcelAnalysisException(BusinessExpEnumerate.COMPANY_NAME_EMPTY.msg());
        }

        PamirsCompany parent = data.getParent();
        if (parent != null && !isBlank.test(parent.getCode())) {
            if (data.getCode().equals(parent.getCode())) {
                throw new ExcelAnalysisException(BusinessExpEnumerate.COMPANY_PARENT_CANNOT.msg());
            }
            LambdaQueryWrapper<PamirsCompany> queryWrapper = Pops.<PamirsCompany>lambdaQuery()
                    .from(PamirsCompany.MODEL_MODEL).eq(PamirsCompany::getCode, parent.getCode());
            Long count = new PamirsCompany().count(queryWrapper);
            if (count <= 0) {
                throw new ExcelAnalysisException(BusinessExpEnumerate.COMPANY_PARENT_NOT_FIND.msg());
            }
        }
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

}
