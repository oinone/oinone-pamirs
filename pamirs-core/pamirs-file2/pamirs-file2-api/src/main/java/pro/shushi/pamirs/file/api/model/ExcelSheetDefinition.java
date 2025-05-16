package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * Excel工作表
 *
 * @author Adamancy Zhang at 15:31 on 2021-08-17
 */
@Base
@Model.model(ExcelSheetDefinition.MODEL_MODEL)
@Model(displayName = "Excel工作表")
public class ExcelSheetDefinition extends TransientModel {

    private static final long serialVersionUID = 8412791335478223852L;

    public static final String MODEL_MODEL = "file.ExcelSheetDefinition";

    @Field(displayName = "工作表名称", summary = "指定工作表名称，当未指定工作表名称时，默认使用模型的显示名称，未绑定模型生成时，默认使用「Sheet + ${index}」作为工作表名称")
    private String name;

    @Field(displayName = "自动列宽", defaultValue = "true", summary = "自动列宽")
    private Boolean autoSizeColumn;

    @Field(displayName = "一次性拿取工作表数据", summary = "该属性仅在包含【固定格式】块时生效，并且所有块的绑定模型必须一致")
    private Boolean onceFetchData;

    @Field(displayName = "区块定义")
    private List<ExcelBlockDefinition> blockDefinitionList;

    @Field(displayName = "单元格合并范围")
    private List<ExcelCellRangeDefinition> mergeRangeList;

    @Field(displayName = "唯一定义")
    private List<ExcelUniqueDefinition> uniqueDefinitions;
}
