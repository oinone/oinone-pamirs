package pro.shushi.pamirs.file.api.model;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelImportModeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelTemplateSourceEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelTemplateTypeEnum;
import pro.shushi.pamirs.file.api.enmu.OfficeVersionEnum;
import pro.shushi.pamirs.file.api.service.ExcelLocationService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * Excel工作簿
 *
 * @author Adamancy Zhang at 15:30 on 2021-08-17
 */
@Base
@Model.model(ExcelWorkbookDefinition.MODEL_MODEL)
@Model.Advanced(unique = {"model,name"}, index = {"model,type"})
@Model(displayName = "Excel工作簿", labelFields = {"displayName"})
public class ExcelWorkbookDefinition extends IdModel implements IDataStatus {

    private static final long serialVersionUID = 4810030577972605665L;

    public static final String MODEL_MODEL = "file.ExcelWorkbookDefinition";

    @Field.String
    @Field(displayName = "名称", required = true, summary = "Excel工作簿的定义名称")
    private String name;

    @Field.String
    @Field(displayName = "显示名称", required = true, summary = "Excel工作簿显示名称")
    private String displayName;

    @Field.String
    @Field(displayName = "文件名", summary = "导出时使用的文件名，不指定则默认使用名称作为文件名")
    private String filename;

    @Field.String
    @Field(displayName = "模型编码", required = true, summary = "模型编码用于决定导入导出展示在哪个模型的table页，并不用于模型化操作")
    private String model;

    @Field.String
    @Field(displayName = "绑定视图名称", summary = "当指定视图时，该模板仅在指定视图中展示")
    private String bindingViewName;

    @Field.Enum
    @Field(displayName = "模板类型", defaultValue = "IMPORT_EXPORT", required = true)
    private ExcelTemplateTypeEnum type;

    @Field.Enum
    @Field(displayName = "Office版本", defaultValue = "AUTO", required = true, summary = "导入时，根据传入的url中指定的文件名后缀进行自动识别，对于无文件名后缀的url必须手动指定；导出时，默认使用新的发行版本；")
    private OfficeVersionEnum version;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "工作表列表", summary = "当指定工作表索引时，有且仅有一个对象；否则根据Excel文件中的sheet个数按顺序排列", store = NullableBoolEnum.FALSE)
    private List<ExcelSheetDefinition> sheetList;

    @Field.String
    @Field(displayName = "下载导入模板重定向地址", store = NullableBoolEnum.FALSE)
    private String redirectUri;

    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "工作表定义JSON字符串", required = true)
    private String sheetDefinitions;

    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "工作簿定义JSON字符串", summary = "缓存工作簿定义的解析内容")
    private String definitionContext;

    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED", required = true)
    private DataStatusEnum dataStatus;

    @Field.String
    @Field(displayName = "导入策略")
    private String importStrategy;

    @Field.String
    @Field(displayName = "导出策略")
    private String exportStrategy;

    @Field.Boolean
    @Field(displayName = "出现错误进行回滚", defaultValue = "false", required = true)
    private Boolean hasErrorRollback;

    @Field.Integer
    @Field(displayName = "最大错误数", defaultValue = "100", required = true)
    private Integer maxErrorLength;

    @Field.Boolean
    @Field(displayName = "清除导出样式", defaultValue = "false", required = true, summary = "使用CSV格式进行导出")
    private Boolean clearExportStyle;

    @Field.Integer
    @Field(displayName = "excel格式导出最大支持行数")
    private Integer excelMaxSupportLength;

    @Field.Integer
    @Field(displayName = "csv格式导出最大支持行数")
    private Integer csvMaxSupportLength;

    @Field.Enum
    @Field(displayName = "模板来源", defaultValue = "CUSTOM")
    private ExcelTemplateSourceEnum templateSource;

    @Field.one2many
    @Field.Relation(relationFields = {"model", "name"}, referenceFields = {"model", "name"})
    @Field(displayName = "国际化配置")
    private List<ExcelLocation> locations;

    @Field.Boolean
    @Field(displayName = "默认是否显示")
    private Boolean defaultShow;

    @Field.Boolean
    @Field(displayName = "是否显示")
    private Boolean show;

    /**
     * @deprecated please using {@link ExcelWorkbookDefinition#importStrategy}
     */
    @Deprecated
    @Field.Boolean
    @Field(displayName = "逐行导入", defaultValue = "false", required = true)
    private Boolean eachImport;

    /**
     * @deprecated please using {@link ExcelBlockDefinition#domain}
     */
    @Deprecated
    @Field.String
    @Field(displayName = "默认过滤规则", summary = "rsql表达式")
    private String domain;

    @Deprecated
    @Field.Enum
    @Field(displayName = "导入模式", summary = "导入模式", defaultValue = "MULTI_MODEL")
    private ExcelImportModeEnum excelImportMode;

    @Deprecated
    @Field.String
    @Field(displayName = "语言", summary = "模板所属语言")
    private String lang;

    public ExcelLocation queryCurrentLocation() {
        TranslateService translateService = TranslateServiceHolder.get();
        String currentLang = translateService.getCurrentLang();
        if (StringUtils.isBlank(currentLang) || !translateService.needTranslate()) {
            return null;
        }
        return BeanDefinitionUtils.getBean(ExcelLocationService.class).queryOneByWrapper(Pops.<ExcelLocation>lambdaQuery()
                .from(ExcelLocation.MODEL_MODEL)
                .eq(ExcelLocation::getModel, getModel())
                .eq(ExcelLocation::getName, getName())
                .eq(ExcelLocation::getLang, currentLang));
    }

    public void storeSheetDefinitions() {
        setSheetDefinitions(UUIDUtil.getUUIDNumberString() + JSONArray.toJSONString(getSheetList()));
    }

    public void analysisSheetDefinitions() {
        setSheetList(JSONArray.parseArray(getSheetDefinitions().substring(32), ExcelSheetDefinition.class));
    }
}
