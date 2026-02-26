package pro.shushi.pamirs.file.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.shushi.pamirs.file.api.enmu.FileExpEnumerate;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.session.tenant.component.PamirsTenantSession;
import pro.shushi.pamirs.meta.api.core.faas.hook.HookApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.constant.FunctionConstants;

import java.io.IOException;

/**
 * {@link ExcelWorkbookDefinition} Controller
 *
 * @author Adamancy Zhang at 22:09 on 2024-06-01
 */
@RestController
public class ExcelWorkbookDefinitionController {

    @Autowired
    private ExcelWorkbookDefinitionService excelWorkbookDefinitionService;

    @Autowired
    private ExcelFileService excelFileService;

    @Resource
    private HookApi hookApi;

    @RequestMapping(value = "/pamirs/file/download/template/{tenant}/{id}", method = RequestMethod.GET)
    public void downloadImportTemplate(@PathVariable("tenant") String tenant,
                                       @PathVariable("id") Long id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        PamirsTenantSession.setTenant(tenant);
        if (id == null) {
            throw new IllegalArgumentException("Invalid template id");
        }
        excelFileService.downloadImportTemplate(fetchWorkbookDefinition(Pops.<ExcelWorkbookDefinition>lambdaQuery()
                .from(ExcelWorkbookDefinition.MODEL_MODEL)
                .eq(ExcelWorkbookDefinition::getId, id)), response);
    }

    @RequestMapping(value = "/pamirs/file/download/template/{tenant}/{model}/{name}", method = RequestMethod.GET)
    public void downloadImportTemplate(@PathVariable("tenant") String tenant,
                                       @PathVariable("model") String model,
                                       @PathVariable("name") String name,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        PamirsTenantSession.setTenant(tenant);
        if (StringUtils.isAnyBlank(model, name)) {
            throw new IllegalArgumentException("Invalid template model/name");
        }
        excelFileService.downloadImportTemplate(fetchWorkbookDefinition(Pops.<ExcelWorkbookDefinition>lambdaQuery()
                .from(ExcelWorkbookDefinition.MODEL_MODEL)
                .eq(ExcelWorkbookDefinition::getModel, model)
                .eq(ExcelWorkbookDefinition::getName, name)), response);
    }

    private ExcelWorkbookDefinition fetchWorkbookDefinition(LambdaQueryWrapper<ExcelWorkbookDefinition> wrapper) {
        hookApi.before(ExcelWorkbookDefinition.MODEL_MODEL, FunctionConstants.queryByWrapper, wrapper);
        ExcelWorkbookDefinition workbookDefinition = excelWorkbookDefinitionService.queryOneByWrapper(wrapper);
        if (workbookDefinition == null) {
            throw PamirsException.construct(FileExpEnumerate.TEMPLATE_NOT_EXIST).errThrow();
        }
        hookApi.after(ExcelWorkbookDefinition.MODEL_MODEL, FunctionConstants.queryByWrapper, workbookDefinition);
        return workbookDefinition;
    }
}
