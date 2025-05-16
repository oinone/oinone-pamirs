package pro.shushi.pamirs.file.api.spi.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.service.ExcelFileService;
import pro.shushi.pamirs.file.api.service.ExcelWorkbookDefinitionService;
import pro.shushi.pamirs.file.api.spi.ExcelInitializeService;
import pro.shushi.pamirs.file.api.spi.ExcelLocationInitializeService;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 默认Excel初始化服务
 *
 * @author Adamancy Zhang at 16:08 on 2024-06-01
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultExcelInitializeService implements ExcelInitializeService {

    @Autowired
    private ExcelFileService excelFileService;

    @Autowired
    private ExcelWorkbookDefinitionService excelWorkbookDefinitionService;

    @Override
    public List<ExcelWorkbookDefinition> generatorTemplates() {
        List<ExcelTemplateInit> initApi = BeanDefinitionUtils.getBeansOfTypeByOrdered(ExcelTemplateInit.class);
        List<ExcelWorkbookDefinition> workbookDefinitions = new ArrayList<>();
        TranslateService translateService = TranslateServiceHolder.get();
        String currentLang = translateService.getCurrentLang();
        for (ExcelTemplateInit init : initApi) {
            try {
                List<ExcelWorkbookDefinition> newWorkbookDefinition = init.generator();
                if (CollectionUtils.isEmpty(newWorkbookDefinition)) {
                    continue;
                }
                for (ExcelWorkbookDefinition item : newWorkbookDefinition) {
                    if (item == null) {
                        continue;
                    }
                    if (StringUtils.isBlank(item.getLang())) {
                        item.setLang(currentLang);
                    }
                    workbookDefinitions.add(item);
                }
            } catch (Throwable e) {
                log.error("Workbook definitions init error. initApi: {}", init.getClass(), e);
            }
        }
        return workbookDefinitions;
    }

    @Override
    public List<ExcelWorkbookDefinition> generatorAfter(List<ExcelWorkbookDefinition> workbookDefinitions) {
        ExcelLocationInitializeService locationInitApi = Spider.getDefaultExtension(ExcelLocationInitializeService.class);
        workbookDefinitions = Optional.ofNullable(workbookDefinitions)
                .map(locationInitApi::init)
                .orElse(null);
        if (CollectionUtils.isNotEmpty(workbookDefinitions)) {
            fillLocations(workbookDefinitions);
        }
        return workbookDefinitions;
    }

    protected void fillLocations(List<ExcelWorkbookDefinition> workbookDefinitions) {
        List<ExcelWorkbookDefinition> targetDefinitions = new ArrayList<>();
        for (ExcelWorkbookDefinition workbookDefinition : workbookDefinitions) {
            if (workbookDefinition.getLocations() == null) {
                targetDefinitions.add(workbookDefinition);
            }
        }
        if (!targetDefinitions.isEmpty()) {
            targetDefinitions = Models.data().listFieldQuery(targetDefinitions, ExcelWorkbookDefinition::getLocations);
            MemoryListSearchCache<String, ExcelWorkbookDefinition> workbookDefinitionCache = new MemoryListSearchCache<>(workbookDefinitions, this::generatorKey);
            for (ExcelWorkbookDefinition targetDefinition : targetDefinitions) {
                List<ExcelLocation> locations = targetDefinition.getLocations();
                if (locations != null) {
                    ExcelWorkbookDefinition target = workbookDefinitionCache.get(generatorKey(targetDefinition));
                    if (target != null) {
                        target.setLocations(locations);
                    }
                }
            }
        }
    }

    private String generatorKey(ExcelWorkbookDefinition workbookDefinition) {
        return workbookDefinition.getModel() + CharacterConstants.SEPARATOR_OCTOTHORPE + workbookDefinition.getName();
    }

    @Override
    public List<ExcelWorkbookDefinition> save(List<ExcelWorkbookDefinition> workbookDefinitions) {
        try {
            return excelWorkbookDefinitionService.createOrUpdateBatch(workbookDefinitions);
        } catch (Throwable e) {
            log.error("Workbook definitions save error.", e);
        }
        return null;
    }

    @Override
    public void saveAfter(List<ExcelWorkbookDefinition> workbookDefinitions) {
        excelFileService.refreshDefinitionContextBatch(workbookDefinitions);
    }
}
