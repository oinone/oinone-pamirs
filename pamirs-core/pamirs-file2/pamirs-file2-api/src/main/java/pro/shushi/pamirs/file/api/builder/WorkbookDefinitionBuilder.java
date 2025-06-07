package pro.shushi.pamirs.file.api.builder;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.builder.BuilderHelper;
import pro.shushi.pamirs.core.common.builder.IBuilder;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;

import java.util.ArrayList;
import java.util.List;

public class WorkbookDefinitionBuilder implements IBuilder<ExcelWorkbookDefinition> {

    private String name;

    private String displayName;

    private String filename;

    private String model;

    private String bindingViewName;

    private ExcelTemplateTypeEnum type = ExcelTemplateTypeEnum.IMPORT_EXPORT;

    private OfficeVersionEnum version = OfficeVersionEnum.AUTO;

    private String importStrategy;

    private String exportStrategy;

    private Boolean hasErrorRollback;

    private Integer maxErrorLength;

    private Boolean clearExportStyle;

    private Integer excelMaxSupportLength;

    private Integer csvMaxSupportLength;

    private Boolean defaultShow = Boolean.TRUE;

    @Deprecated
    private Boolean eachImport;

    @Deprecated
    private String domain;

    @Deprecated
    private String lang;

    @Deprecated
    private ExcelImportModeEnum excelImportMode = ExcelImportModeEnum.MULTI_MODEL;

    private List<IBuilder<ExcelSheetDefinition>> sheetBuilderList = new ArrayList<>();

    public WorkbookDefinitionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public WorkbookDefinitionBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public WorkbookDefinitionBuilder setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public WorkbookDefinitionBuilder setModel(String model) {
        this.model = model;
        return this;
    }

    public void setBindingViewName(String bindingViewName) {
        this.bindingViewName = bindingViewName;
    }

    public WorkbookDefinitionBuilder setType(ExcelTemplateTypeEnum type) {
        this.type = type;
        return this;
    }

    public WorkbookDefinitionBuilder setVersion(OfficeVersionEnum version) {
        this.version = version;
        return this;
    }

    public WorkbookDefinitionBuilder setImportStrategy(String importStrategy) {
        this.importStrategy = importStrategy;
        return this;
    }

    public WorkbookDefinitionBuilder setImportStrategy(ExcelImportStrategyEnum importStrategy) {
        if (importStrategy == null) {
            this.importStrategy = null;
        } else {
            this.importStrategy = importStrategy.value();
        }
        return this;
    }

    public WorkbookDefinitionBuilder setExportStrategy(String exportStrategy) {
        this.exportStrategy = exportStrategy;
        return this;
    }

    public WorkbookDefinitionBuilder setExportStrategy(ExcelExportStrategyEnum exportStrategy) {
        if (exportStrategy == null) {
            this.exportStrategy = null;
        } else {
            this.exportStrategy = exportStrategy.value();
        }
        return this;
    }

    public WorkbookDefinitionBuilder setHasErrorRollback(Boolean hasErrorRollback) {
        this.hasErrorRollback = hasErrorRollback;
        return this;
    }

    public WorkbookDefinitionBuilder setMaxErrorLength(Integer maxErrorLength) {
        this.maxErrorLength = maxErrorLength;
        return this;
    }

    public WorkbookDefinitionBuilder setClearExportStyle(Boolean clearExportStyle) {
        this.clearExportStyle = clearExportStyle;
        return this;
    }

    public WorkbookDefinitionBuilder setExcelMaxSupportLength(Integer excelMaxSupportLength) {
        this.excelMaxSupportLength = excelMaxSupportLength;
        return this;
    }

    public WorkbookDefinitionBuilder setCsvMaxSupportLength(Integer csvMaxSupportLength) {
        this.csvMaxSupportLength = csvMaxSupportLength;
        return this;
    }

    public WorkbookDefinitionBuilder setDefaultShow(Boolean defaultShow) {
        this.defaultShow = defaultShow;
        return this;
    }

    @Deprecated
    public WorkbookDefinitionBuilder setEachImport(Boolean eachImport) {
        this.eachImport = eachImport;
        return this;
    }

    @Deprecated
    public WorkbookDefinitionBuilder setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    @Deprecated
    public WorkbookDefinitionBuilder setLang(String lang) {
        this.lang = lang;
        return this;
    }

    @Deprecated
    public WorkbookDefinitionBuilder setExcelImportMode(ExcelImportModeEnum excelImportMode) {
        this.excelImportMode = excelImportMode;
        return this;
    }

    public List<IBuilder<ExcelSheetDefinition>> getSheetBuilderList() {
        return sheetBuilderList;
    }

    public WorkbookDefinitionBuilder setSheetBuilderList(List<IBuilder<ExcelSheetDefinition>> sheetBuilderList) {
        this.sheetBuilderList = sheetBuilderList;
        return this;
    }

    private WorkbookDefinitionBuilder(String model, String name) {
        this.model = model;
        this.name = name;
    }

    public static WorkbookDefinitionBuilder newInstance(String model, String name) {
        return new WorkbookDefinitionBuilder(model, name);
    }

    public SheetDefinitionBuilder createSheet() {
        SheetDefinitionBuilder builder = new SheetDefinitionBuilder(this);
        this.sheetBuilderList.add(builder);
        return builder;
    }

    public SheetDefinitionBuilder createSheet(String sheetName) {
        SheetDefinitionBuilder builder = new SheetDefinitionBuilder(this);
        this.sheetBuilderList.add(builder.setName(sheetName));
        return builder;
    }

    @Override
    public ExcelWorkbookDefinition build() {
        if (StringUtils.isBlank(displayName)) {
            displayName = name;
        }
        ExcelWorkbookDefinition workbookDefinition = new ExcelWorkbookDefinition()
                .setName(name)
                .setDisplayName(displayName)
                .setFilename(filename)
                .setModel(model)
                .setBindingViewName(bindingViewName)
                .setType(type)
                .setVersion(version)
                .setImportStrategy(importStrategy)
                .setExportStrategy(exportStrategy)
                .setSheetList(BuilderHelper.batchBuild(sheetBuilderList))
                .setDataStatus(DataStatusEnum.ENABLED)
                .setHasErrorRollback(hasErrorRollback)
                .setMaxErrorLength(maxErrorLength)
                .setClearExportStyle(clearExportStyle)
                .setExcelMaxSupportLength(excelMaxSupportLength)
                .setCsvMaxSupportLength(csvMaxSupportLength)
                .setDefaultShow(defaultShow)
                .setTemplateSource(ExcelTemplateSourceEnum.INITIALIZATION)
                .setEachImport(eachImport)
                .setDomain(domain)
                .setExcelImportMode(excelImportMode)
                .setLang(lang);
        if (StringUtils.isBlank(lang)) {
            TranslateService translateService = TranslateServiceHolder.get();
            workbookDefinition.setLang(translateService.getCurrentLang());
        }
        workbookDefinition.storeSheetDefinitions();
        return workbookDefinition;
    }
}
