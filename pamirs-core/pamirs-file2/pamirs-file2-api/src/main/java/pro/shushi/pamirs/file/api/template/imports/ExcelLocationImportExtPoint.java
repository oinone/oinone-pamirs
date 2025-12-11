package pro.shushi.pamirs.file.api.template.imports;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.file.api.model.ExcelLocationItem;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.service.ExcelLocationService;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.file.api.template.ExcelLocationTemplate;
import pro.shushi.pamirs.file.api.template.entity.ExcelLocationData;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;

/**
 * {@link ExcelLocation} import ext point
 *
 * @author Adamancy Zhang at 17:15 on 2024-06-01
 */
@Base
@Component
@Ext(ExcelImportTask.class)
public class ExcelLocationImportExtPoint extends AbstractExcelImportDataExtPointImpl<ExcelLocationData> {

    @Autowired
    private ExcelWorkbookDefinitionService excelWorkbookDefinitionService;

    @Autowired
    private ExcelFileService excelFileService;

    @Autowired
    private ExcelLocationService excelLocationService;

    @ExtPoint.Implement(expression = "importContext.definitionContext.model == \"" + ExcelWorkbookDefinition.MODEL_MODEL + "\" && importContext.definitionContext.name == \"" + ExcelLocationTemplate.TEMPLATE_NAME + "\"")
    @Override
    public Boolean importData(ExcelImportContext importContext, ExcelLocationData data) {
        Map<String, ExcelLocation> locationMap = importContext.getDataBuffer(0, HashMap::new);
        if (importContext.getCurrentListener().hasNext()) {
            String model = data.getModel();
            if (StringUtils.isBlank(model)) {
                return Boolean.TRUE;
            }
            String name = data.getName();
            if (StringUtils.isBlank(name)) {
                return Boolean.TRUE;
            }
            String lang = data.getTargetLang();
            if (StringUtils.isBlank(lang)) {
                return Boolean.TRUE;
            }
            String origin = data.getOrigin();
            if (StringUtils.isBlank(origin)) {
                return Boolean.TRUE;
            }
            String target = data.getTarget();
            if (StringUtils.isBlank(target)) {
                return Boolean.TRUE;
            }
            ExcelLocation location = locationMap.computeIfAbsent(model + CharacterConstants.SEPARATOR_OCTOTHORPE + name + CharacterConstants.SEPARATOR_OCTOTHORPE + lang,
                    k -> new ExcelLocation()
                            .setModel(model)
                            .setName(name)
                            .setLang(lang));
            List<ExcelLocationItem> locationItems = location.getLocationItems();
            if (locationItems == null) {
                locationItems = new ArrayList<>();
                location.setLocationItems(locationItems);
            }
            locationItems.add(new ExcelLocationItem().setOrigin(origin).setTarget(target));
            return Boolean.TRUE;
        }
        List<ExcelLocation> locations = new ArrayList<>(locationMap.values());
        if (!locations.isEmpty()) {
            excelLocationService.createOrUpdateBatch(locations);
            List<String> models = new ArrayList<>();
            List<String> names = new ArrayList<>();
            Set<String> repeatSet = new HashSet<>();
            for (ExcelLocation location : locations) {
                String model = location.getModel();
                String name = location.getName();
                if (ObjectHelper.isNotRepeat(repeatSet, model + CharacterConstants.SEPARATOR_OCTOTHORPE + name)) {
                    models.add(model);
                    names.add(name);
                }
            }
            List<ExcelWorkbookDefinition> workbookDefinitions = DataShardingHelper.build().sharding(models.size(), (begin, end, page, size) -> excelWorkbookDefinitionService.queryListByWrapper(Pops.<ExcelWorkbookDefinition>lambdaQuery()
                    .from(ExcelWorkbookDefinition.MODEL_MODEL)
                    .in(Lists.newArrayList(ExcelWorkbookDefinition::getModel, ExcelWorkbookDefinition::getName),
                            new ArrayList<>(models.subList(begin, end)),
                            new ArrayList<>(names.subList(begin, end)))));
            if (!workbookDefinitions.isEmpty()) {
                workbookDefinitions = Models.origin().listFieldQuery(workbookDefinitions, ExcelWorkbookDefinition::getLocations);
                excelFileService.refreshDefinitionContextBatch(workbookDefinitions);
            }
        }
        return Boolean.TRUE;
    }
}
