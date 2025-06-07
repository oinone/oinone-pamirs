package pro.shushi.pamirs.file.api.context;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.file.api.enmu.ExcelExportStrategyEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelImportModeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelImportStrategyEnum;
import pro.shushi.pamirs.file.api.enmu.OfficeVersionEnum;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Excel定义上下文
 *
 * @author Adamancy Zhang at 09:50 on 2021-03-11
 */
public class ExcelDefinitionContext implements Serializable {

    private static final long serialVersionUID = -8689740008058064822L;

    public static final String EXCEL_TX_CONFIG_PREFIX = "EXCEL_IMPORT_";

    /**
     * SheetDefinition的JSON字符串的哈希值
     */
    private int hashCode;

    /**
     * SheetDefinition的JSON字符串的前32位
     */
    private String scope;

    /**
     * 标记该上下文是否需要刷新（非存储字段）
     */
    @JSONField(serialize = false)
    private boolean isRefresh = false;

    /**
     * 模板Id
     */
    private Long templateId;

    /**
     * 导入/导出文件名
     */
    private String filename;

    /**
     * 模型
     */
    private String model;

    /**
     * 名称
     */
    private String name;

    /**
     * Excel版本
     */
    private OfficeVersionEnum version;

    /**
     * 原始工作表定义
     */
    @JSONField(serialize = false)
    private transient List<ExcelSheetDefinition> originSheetList;

    /**
     * 工作表定义（仅执行初始化的时候存在）
     */
    private List<EasyExcelSheetDefinition> sheetList;

    /**
     * 导入策略
     */
    private ExcelImportStrategyEnum importStrategy;

    /**
     * 导出策略
     */
    private ExcelExportStrategyEnum exportStrategy;

    /**
     * 国际化配置
     */
    private Map<String, Map<String, String>> locations;

    /**
     * 当前语言
     */
    @JSONField(serialize = false)
    private transient String currentLang;

    @JSONField(serialize = false)
    private transient Map<String, String> currentLocation;

    /**
     * 导入模式
     */
    @Deprecated
    private ExcelImportModeEnum excelImportMode;

    public int getHashCode() {
        return hashCode;
    }

    public ExcelDefinitionContext setHashCode(int hashCode) {
        this.hashCode = hashCode;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public ExcelDefinitionContext setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public boolean getIsRefresh() {
        return isRefresh;
    }

    public ExcelDefinitionContext setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
        return this;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public ExcelDefinitionContext setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public ExcelDefinitionContext setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getModel() {
        return model;
    }

    public ExcelDefinitionContext setModel(String model) {
        this.model = model;
        return this;
    }

    public String getName() {
        return name;
    }

    public ExcelDefinitionContext setName(String name) {
        this.name = name;
        return this;
    }

    public OfficeVersionEnum getVersion() {
        return version;
    }

    public ExcelDefinitionContext setVersion(OfficeVersionEnum version) {
        this.version = version;
        return this;
    }

    public List<ExcelSheetDefinition> getOriginSheetList() {
        return originSheetList;
    }

    public ExcelDefinitionContext setOriginSheetList(List<ExcelSheetDefinition> originSheetList) {
        this.originSheetList = originSheetList;
        return this;
    }

    public List<EasyExcelSheetDefinition> getSheetList() {
        return sheetList;
    }

    public ExcelDefinitionContext setSheetList(List<EasyExcelSheetDefinition> sheetList) {
        this.sheetList = sheetList;
        return this;
    }

    public ExcelImportStrategyEnum getImportStrategy() {
        return importStrategy;
    }

    public ExcelDefinitionContext setImportStrategy(ExcelImportStrategyEnum importStrategy) {
        this.importStrategy = importStrategy;
        return this;
    }

    public ExcelExportStrategyEnum getExportStrategy() {
        return exportStrategy;
    }

    public ExcelDefinitionContext setExportStrategy(ExcelExportStrategyEnum exportStrategy) {
        this.exportStrategy = exportStrategy;
        return this;
    }

    public ExcelImportModeEnum getExcelImportMode() {
        return excelImportMode;
    }

    public ExcelDefinitionContext setExcelImportMode(ExcelImportModeEnum excelImportMode) {
        this.excelImportMode = excelImportMode;
        return this;
    }

    public Map<String, Map<String, String>> getLocations() {
        return locations;
    }

    public ExcelDefinitionContext setLocations(Map<String, Map<String, String>> locations) {
        this.locations = locations;
        return this;
    }

    public ExcelDefinitionContext setCurrentLang(String currentLang) {
        this.currentLang = currentLang;
        return this;
    }

    public ExcelDefinitionContext setCurrentLocation(Map<String, String> currentLocation) {
        this.currentLocation = currentLocation;
        return this;
    }

    public boolean isNeedTranslate() {
        if (StringUtils.isBlank(currentLang)) {
            return false;
        }
        return Optional.ofNullable(locations)
                .map(v -> v.get(currentLang))
                .map(MapUtils::isNotEmpty)
                .orElse(false);
    }

    public String translate(String origin) {
        if (StringUtils.isAnyBlank(currentLang, origin)) {
            return origin;
        }
        if (currentLocation == null) {
            synchronized (this) {
                if (currentLocation == null) {
                    currentLocation = Optional.ofNullable(locations)
                            .map(v -> v.get(currentLang))
                            .orElse(Collections.emptyMap());
                }
            }
        }
        String target = currentLocation.get(origin);
        if (StringUtils.isBlank(target)) {
            return origin;
        }
        return target;
    }
}
