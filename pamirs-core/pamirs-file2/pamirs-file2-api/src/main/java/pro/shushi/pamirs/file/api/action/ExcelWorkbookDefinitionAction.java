package pro.shushi.pamirs.file.api.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.FileExpEnumerate;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.file.api.util.ExcelWorkbookDefinitionUtil;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * {@link ExcelWorkbookDefinition}动作
 *
 * @author Adamancy Zhang at 12:26 on 2020-11-10
 */
@Component
@Model.model(ExcelWorkbookDefinition.MODEL_MODEL)
public class ExcelWorkbookDefinitionAction {

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ExcelWorkbookDefinitionService excelWorkbookDefinitionService;

    @Autowired
    private ExcelFileService excelFileService;

    @Function.Advanced(displayName = "查询Excel工作簿定义列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<ExcelWorkbookDefinition> queryPage(Pagination<ExcelWorkbookDefinition> page, IWrapper<ExcelWorkbookDefinition> queryWrapper) {
        Pagination<ExcelWorkbookDefinition> pagination = excelWorkbookDefinitionService.queryPage(page, WrapperHelper.lambda(queryWrapper));

        // 设置当前语言
        TranslateService translateService = TranslateServiceHolder.get();
        String currentLang = translateService.getCurrentLang();

        if (StringUtils.isNotBlank(currentLang) && translateService.needTranslate()) {
            List<ExcelWorkbookDefinition> workbookDefinitions = pagination.getContent();
            if (CollectionUtils.isNotEmpty(workbookDefinitions)) {
                for (ExcelWorkbookDefinition workbookDefinition : workbookDefinitions) {
                    if (StringUtils.isBlank(workbookDefinition.getDefinitionContext())) {
                        continue;
                    }
                    workbookDefinition.analysisSheetDefinitions();
                    ExcelDefinitionContext definitionContext = ExcelWorkbookDefinitionUtil.getDefinitionContext(workbookDefinition);
                    definitionContext.setCurrentLang(currentLang);
                    workbookDefinition.setDisplayName(definitionContext.translate(workbookDefinition.getDisplayName()));
                }
            }
        }
        return pagination;
    }

    @Function.Advanced(displayName = "查询指定Excel工作簿定义", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_ONE, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public ExcelWorkbookDefinition queryOne(ExcelWorkbookDefinition query) {
        ExcelWorkbookDefinition origin = excelWorkbookDefinitionService.queryOne(query);
        if (origin == null) {
            throw PamirsException.construct(FileExpEnumerate.TEMPLATE_NOT_EXIST).errThrow();
        }
        if (StringUtils.isNotBlank(origin.getDefinitionContext())) {
            origin.analysisSheetDefinitions();
        }
        return origin;
    }

    @Function.Advanced(displayName = "获取导入模板URL", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public ExcelWorkbookDefinition fetchImportTemplateUrl(ExcelWorkbookDefinition data) {
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String refererUrl = request.getHeader("Referer");
            if (StringUtils.isBlank(refererUrl)) {
                return data.setRedirectUri(String.format("/pamirs/file/download/template/%s/%d", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getId()));
            } else {
                // 存在base64编码的情况
                if (refererUrl.contains("?")) {
                    // http://localhost:8080/cnjk/page?O21vZHVsZT1Fc0FkbWluO3ZpZXdUeXBlPVRBQkxFO21vZGVsPWFkbWluLkFkbWluU3RvcmFnZVN0YXRpb25Qcm94eTthY3Rpb249RW5lcmd5TWVudV9DdXN0b21lcl9TdG9yYWdlU3RhdGlvbk1lbnU7c2NlbmU9RW5lcmd5TWVudV9DdXN0b21lcl9TdG9yYWdlU3RhdGlvbk1lbnU7dGFyZ2V0PU9QRU5fV0lORE9XO21lbnU9JTdCJTIyc2VsZWN0ZWRLZXlzJTIyOiU1QiUyMkVuZXJneU1lbnVfQ3VzdG9tZXJfU3RvcmFnZVN0YXRpb25NZW51JTIyJTVELCUyMm9wZW5LZXlzJTIyOiU1QiUyMkVuZXJneU1lbnVfQ3VzdG9tZXIlMjIlNUQlN0Q=
                    String baseUrl = refererUrl.substring(0, refererUrl.indexOf("?")).replace("page", "");
                    return data.setRedirectUri(String.format(baseUrl + "pamirs/file/download/template/%s/%d", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getId()));
                } else {
                    // https://one.oinone.top/page;module=ys0328;viewType=TABLE;model=ys0328.k2.Model0000000453;action=MenuuiMenu0000000000050002;scene=MenuuiMenu0000000000050002;target=OPEN_WINDOW;menu=%7B%22selectedKeys%22:%5B%22uiMenu0000000000050002%22%5D,%22openKeys%22:%5B%22uiMenu0000000000059501%22%5D%7D
                    String baseUrl = refererUrl.substring(0, refererUrl.indexOf(";")).replace("page", "");
                    return data.setRedirectUri(String.format(baseUrl + "pamirs/file/download/template/%s/%d", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getId()));
                }
            }
        } else {
            return data.setRedirectUri(String.format("/pamirs/file/download/template/%s/%d", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getId()));
        }
    }

    @Function.Advanced(displayName = "通过模型和名称获取导入模板URL", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE})
    public ExcelWorkbookDefinition fetchImportTemplateUrlByName(ExcelWorkbookDefinition data) {
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String refererUrl = request.getHeader("Referer");
            if (StringUtils.isBlank(refererUrl)) {
                return data.setRedirectUri(String.format("/pamirs/file/download/template/%s/%s/%s", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getModel(), data.getName()));
            } else {
                if (refererUrl.contains("?")) {
                    // http://localhost:8080/cnjk/page?O21vZHVsZT1Fc0FkbWluO3ZpZXdUeXBlPVRBQkxFO21vZGVsPWFkbWluLkFkbWluU3RvcmFnZVN0YXRpb25Qcm94eTthY3Rpb249RW5lcmd5TWVudV9DdXN0b21lcl9TdG9yYWdlU3RhdGlvbk1lbnU7c2NlbmU9RW5lcmd5TWVudV9DdXN0b21lcl9TdG9yYWdlU3RhdGlvbk1lbnU7dGFyZ2V0PU9QRU5fV0lORE9XO21lbnU9JTdCJTIyc2VsZWN0ZWRLZXlzJTIyOiU1QiUyMkVuZXJneU1lbnVfQ3VzdG9tZXJfU3RvcmFnZVN0YXRpb25NZW51JTIyJTVELCUyMm9wZW5LZXlzJTIyOiU1QiUyMkVuZXJneU1lbnVfQ3VzdG9tZXIlMjIlNUQlN0Q=
                    String baseUrl = refererUrl.substring(0, refererUrl.indexOf("?")).replace("page", "");
                    return data.setRedirectUri(baseUrl + String.format("pamirs/file/download/template/%s/%s/%s", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getModel(), data.getName()));
                } else {
                    // https://one.oinone.top/page;module=ys0328;viewType=TABLE;model=ys0328.k2.Model0000000453;action=MenuuiMenu0000000000050002;scene=MenuuiMenu0000000000050002;target=OPEN_WINDOW;menu=%7B%22selectedKeys%22:%5B%22uiMenu0000000000050002%22%5D,%22openKeys%22:%5B%22uiMenu0000000000059501%22%5D%7D
                    String baseUrl = refererUrl.substring(0, refererUrl.indexOf(";")).replace("page", "");
                    return data.setRedirectUri(baseUrl + String.format("pamirs/file/download/template/%s/%s/%s", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getModel(), data.getName()));
                }
            }
        } else {
            return data.setRedirectUri(String.format("/pamirs/file/download/template/%s/%s/%s", Optional.ofNullable(PamirsTenantSession.getTenant()).orElse("pamirs"), data.getModel(), data.getName()));
        }
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.type)", error = "模板类型不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.version)", error = "Office版本不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.sheetDefinitions)", error = "工作表JSON不能为空"),
    })
    @Action(displayName = "创建")
    public ExcelWorkbookDefinition create(ExcelWorkbookDefinition data) {
        return data.create();
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.name)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.type)", error = "模板类型不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.version)", error = "Office版本不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.sheetDefinitions)", error = "工作表JSON不能为空"),
    })
    @Action(displayName = "更新")
    public ExcelWorkbookDefinition update(ExcelWorkbookDefinition data) {
        data.updateById();
        return data;
    }

}
