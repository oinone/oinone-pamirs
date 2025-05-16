package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Base
@Model.model(ExcelBlockDefinition.MODEL_MODEL)
@Model(displayName = "Excel区块")
public class ExcelBlockDefinition extends TransientModel {

    private static final long serialVersionUID = 4620470770162678085L;

    public static final String MODEL_MODEL = "file.ExcelBlockDefinition";

    @Field(displayName = "绑定模型", summary = "在绑定模型后，可使用默认模板及模型解析功能")
    private String bindingModel;

    @Field(displayName = "获取函数命名空间")
    private String fetchNamespace;

    @Field(displayName = "获取函数名称")
    private String fetchFun;

    @Field(displayName = "默认过滤规则", summary = "rsql表达式")
    private String domain;

    @Field(displayName = "解析类型", summary = "指定工作表所使用的解析类型，不同的解析类型所需的定义方式存在差异", required = true)
    private ExcelAnalysisTypeEnum analysisType;

    @Field(displayName = "排列方向", summary = "指定当前表头行的排列方向", required = true)
    private ExcelDirectionEnum direction;

    @Field(displayName = "设计范围", required = true)
    private ExcelCellRangeDefinition designRange;

    @Field(displayName = "是否使用层叠样式", summary = "将样式覆盖变为样式层叠；单个区块有效；优先级顺序为：表头行样式 < 数据行样式 < 单元格样式")
    private Boolean usingCascadingStyle;

    @Field(displayName = "预置数", summary = "使用空的字符串填充数据行的单元格")
    private Integer presetNumber;

    @Field(displayName = "表头定义")
    private List<ExcelHeaderDefinition> headerList;

    @Field(displayName = "行定义")
    private List<ExcelRowDefinition> rowList;

    @Field(displayName = "单元格合并范围")
    private List<ExcelCellRangeDefinition> mergeRangeList;

    @Field(displayName = "唯一定义")
    private List<ExcelUniqueDefinition> uniqueDefinitions;
}
