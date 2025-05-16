package pro.shushi.pamirs.file.api.template.exports;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.cache.MemoryIterableSearchCache;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.entity.ExcelExportFetchDataContext;
import pro.shushi.pamirs.file.api.executor.ExcelExportExecutor;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelBlockFetchDataExtPoint;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.service.ExcelLocationService;
import pro.shushi.pamirs.file.api.template.ExcelLocationTemplate;
import pro.shushi.pamirs.file.api.template.entity.ExcelLocationData;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;

import java.util.*;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SS;

/**
 * {@link ExcelLocation} export ext point
 *
 * @author Adamancy Zhang at 17:15 on 2024-06-01
 */
@Base
@Component
@Ext(ExcelExportExecutor.class)
public class ExcelLocationExportExtPoint extends AbstractExcelBlockFetchDataExtPoint<ExcelWorkbookDefinition> {

    @ExtPoint.Implement(expression = "context.context.model == \"" + ExcelWorkbookDefinition.MODEL_MODEL + "\" & context.context.name == \"" + ExcelLocationTemplate.TEMPLATE_NAME + "\"")
    @Override
    public Object fetchExportData(ExcelExportFetchDataContext context) {
        List<ExcelWorkbookDefinition> workbookDefinitions = FetchUtil.cast(super.fetchExportData(context));
        if (CollectionUtils.isEmpty(workbookDefinitions)) {
            return new ArrayList<>();
        }
        String lang = FetchUtil.fetchLang();
        List<ExcelLocation> locations;
        if (StringUtils.isBlank(lang)) {
            locations = Collections.emptyList();
        } else {
            locations = genericFetchExportData(ExcelLocationService.FUN_NAMESPACE, FunctionConstants.queryListByWrapper,
                    Pops.<ExcelLocation>lambdaQuery()
                            .from(ExcelLocation.MODEL_MODEL)
                            .eq(ExcelLocation::getLang, lang)
            );
        }
        return generatorLocationDataList(workbookDefinitions, locations, lang);
    }

    private List<ExcelLocationData> generatorLocationDataList(List<ExcelWorkbookDefinition> workbookDefinitions, List<ExcelLocation> locations, String targetLang) {
        ExcelLocationIterator iterator = new ExcelLocationIterator(locations);
        MemoryIterableSearchCache<String, ExcelLocationData> locationCache = new MemoryIterableSearchCache<String, ExcelLocationData>(iterator, ExcelLocationExportExtPoint::generatorLocationUniqueKey);
        List<ExcelLocationData> locationDataList = new ArrayList<>();
        for (ExcelWorkbookDefinition workbookDefinition : workbookDefinitions) {
            ExcelLocationGeneratorContext context = new ExcelLocationGeneratorContext(workbookDefinition, locationCache, targetLang);
            if (StringUtils.isNotBlank(workbookDefinition.getSheetDefinitions())) {
                workbookDefinition.analysisSheetDefinitions();
            }
            context.append(workbookDefinition.getDisplayName());
            context.append(workbookDefinition.getFilename());
            List<ExcelSheetDefinition> sheetList = workbookDefinition.getSheetList();
            if (CollectionUtils.isNotEmpty(sheetList)) {
                for (ExcelSheetDefinition sheetDefinition : sheetList) {
                    generatorLocationData(context, sheetDefinition);
                }
            }
            locationDataList.addAll(context.locationDataList);
        }
        return locationDataList;
    }

    private static String getSystemOriginLang() {
        return DefaultResourceConstants.CHINESE_LANGUAGE_CODE;
    }

    private void generatorLocationData(ExcelLocationGeneratorContext context, ExcelSheetDefinition sheetDefinition) {
        context.append(sheetDefinition.getName());
        List<ExcelBlockDefinition> blockDefinitionList = sheetDefinition.getBlockDefinitionList();
        if (CollectionUtils.isEmpty(blockDefinitionList)) {
            return;
        }
        for (ExcelBlockDefinition blockDefinition : blockDefinitionList) {
            generatorLocationData(context, blockDefinition);
        }
    }

    private void generatorLocationData(ExcelLocationGeneratorContext context, ExcelBlockDefinition blockDefinition) {
        List<ExcelHeaderDefinition> headerList = blockDefinition.getHeaderList();
        if (CollectionUtils.isNotEmpty(headerList)) {
            for (ExcelHeaderDefinition headerDefinition : headerList) {
                generatorLocationData(context, headerDefinition);
            }
        }
        List<ExcelRowDefinition> rowList = blockDefinition.getRowList();
        if (CollectionUtils.isNotEmpty(rowList)) {
            for (ExcelRowDefinition rowDefinition : rowList) {
                generatorLocationData(context, rowDefinition);
            }
        }
    }

    private <T extends ExcelRowDefinition> void generatorLocationData(ExcelLocationGeneratorContext context, T rowDefinition) {
        List<ExcelCellDefinition> cellList = rowDefinition.getCellList();
        if (CollectionUtils.isEmpty(cellList)) {
            return;
        }
        for (ExcelCellDefinition cellDefinition : cellList) {
            context.append(cellDefinition.getValue());
            ExcelValueTypeEnum type = cellDefinition.getType();
            if (ExcelValueTypeEnum.BOOLEAN.equals(type) || ExcelValueTypeEnum.ENUMERATION.equals(type)) {
                Optional.ofNullable(cellDefinition.getFormat())
                        .filter(StringUtils::isNotBlank)
                        .map(v -> JSON.<Map<String, String>>parseObject(v, TR_MAP_SS.getType(), Feature.OrderedField))
                        .map(Map::values)
                        .ifPresent(enumerationValues -> {
                            for (String enumerationValue : enumerationValues) {
                                context.append(enumerationValue);
                            }
                        });
            }
        }
    }

    private static String generatorLocationUniqueKey(ExcelLocationData data) {
        return generatorLocationUniqueKey(data.getModel(), data.getName(), data.getOrigin());
    }

    private static String generatorLocationUniqueKey(ExcelWorkbookDefinition workbookDefinition, String origin) {
        return generatorLocationUniqueKey(workbookDefinition.getModel(), workbookDefinition.getName(), origin);
    }

    private static String generatorLocationUniqueKey(String model, String name, String origin) {
        return model + CharacterConstants.SEPARATOR_OCTOTHORPE + name + CharacterConstants.SEPARATOR_OCTOTHORPE + origin;
    }

    @Override
    protected IWrapper<ExcelWorkbookDefinition> generatorWrapper(ExcelExportFetchDataContext context) {
        return Pops.<ExcelWorkbookDefinition>lambdaQuery()
                .from(ExcelWorkbookDefinition.MODEL_MODEL)
                .eq(ExcelWorkbookDefinition::getDefaultShow, Boolean.TRUE)
                .and(w -> w.isNull(ExcelWorkbookDefinition::getShow).or().eq(ExcelWorkbookDefinition::getShow, Boolean.TRUE))
                .ge(ExcelWorkbookDefinition::getId, 0L);
    }

    private static class ExcelLocationGeneratorContext {

        private final String model;

        private final String name;

        private final String displayName;

        private final Set<String> repeatSet;

        private final List<ExcelLocationData> locationDataList;

        private final MemoryIterableSearchCache<String, ExcelLocationData> locationCache;

        private final String targetLang;

        public ExcelLocationGeneratorContext(ExcelWorkbookDefinition workbookDefinition, MemoryIterableSearchCache<String, ExcelLocationData> locationCache, String targetLang) {
            this.model = workbookDefinition.getModel();
            this.name = workbookDefinition.getName();
            this.displayName = workbookDefinition.getDisplayName();
            this.repeatSet = new HashSet<>();
            this.locationDataList = new ArrayList<>();
            this.locationCache = locationCache;
            this.targetLang = targetLang;
        }

        public void append(String origin) {
            if (StringUtils.isBlank(origin)) {
                return;
            }
            ExcelLocationData locationData = getLocationData(origin);
            if (locationData == null) {
                locationData = new ExcelLocationData()
                        .setModel(model)
                        .setName(name)
                        .setDisplayName(displayName)
                        .setOriginLang(getSystemOriginLang())
                        .setOrigin(origin);
            }
            append(locationData);
        }

        private void append(ExcelLocationData locationData) {
            if (ObjectHelper.isRepeat(repeatSet, generatorLocationUniqueKey(locationData))) {
                return;
            }
            locationData.setDisplayName(displayName);
            locationData.setTargetLang(targetLang);
            locationDataList.add(locationData);
        }

        private ExcelLocationData getLocationData(String origin) {
            return locationCache.get(generatorLocationUniqueKey(model, name, origin));
        }
    }

    private static class ExcelLocationIterator implements Iterator<ExcelLocationData>, Iterable<ExcelLocationData> {

        private final Iterator<ExcelLocation> locationIterator;

        private Iterator<ExcelLocationItem> locationItemIterator;

        private boolean isStart;

        private ExcelLocation current;

        private ExcelLocationItem currentItem;

        public ExcelLocationIterator(List<ExcelLocation> locations) {
            this.isStart = false;
            this.locationIterator = locations.iterator();
        }

        @Override
        public boolean hasNext() {
            if (!this.isStart) {
                return true;
            }
            return locationIterator.hasNext() || (locationItemIterator != null && locationItemIterator.hasNext());
        }

        @Override
        public ExcelLocationData next() {
            if (!this.isStart) {
                this.isStart = true;
                return init();
            }
            if (nextLocationItem()) {
                return generatorData();
            }
            if (nextLocation()) {
                return generatorData();
            }
            return null;
        }

        private ExcelLocationData init() {
            if (!nextLocation()) {
                return null;
            }
            return generatorData();
        }

        private ExcelLocationData generatorData() {
            return new ExcelLocationData()
                    .setModel(current.getModel())
                    .setName(current.getName())
                    .setOriginLang(getSystemOriginLang())
                    .setTargetLang(current.getLang())
                    .setOrigin(currentItem.getOrigin())
                    .setTarget(currentItem.getTarget());
        }

        private boolean nextLocation() {
            if (locationIterator.hasNext()) {
                current = locationIterator.next();
                List<ExcelLocationItem> locationItems = current.getLocationItems();
                if (CollectionUtils.isEmpty(locationItems)) {
                    currentItem = null;
                    locationItemIterator = null;
                } else {
                    locationItemIterator = locationItems.iterator();
                    return nextLocationItem();
                }
                return true;
            }
            return false;
        }

        private boolean nextLocationItem() {
            if (locationItemIterator.hasNext()) {
                currentItem = locationItemIterator.next();
                return true;
            }
            return false;
        }

        @NonNull
        @Override
        public Iterator<ExcelLocationData> iterator() {
            return this;
        }
    }
}
