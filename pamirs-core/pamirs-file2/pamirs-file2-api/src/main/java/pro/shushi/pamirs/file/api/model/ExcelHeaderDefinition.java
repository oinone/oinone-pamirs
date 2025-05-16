package pro.shushi.pamirs.file.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * <h>Excel表头行定义</h>
 * <p>
 * 1、固定表头解析模式中，Excel表头分为两种:<br>
 * - 配置行: 即属性配置行，用于Excel导入/导出时与JavaBean对象之间的属性映射。<br>
 * - 表头行: 在导入/导出时直接展示给用户的表头行。<br>
 * 在导入时，也作为模板是否匹配的验证依据，只有导入所用Excel表头与系统中存储的Excel表头完全一致的情况下，才认为该导入文件是有效的。<br>
 * 2、固定格式解析模式中，Excel表头仅有一种:<br>
 * - 配置行: 配置行的唯一作用为定义自动列宽或者固定列宽，无其他作用。<br>
 * </p>
 *
 * @author Adamancy Zhang at 17:25 on 2021-08-14
 */
@Base
@Model.model(ExcelHeaderDefinition.MODEL_MODEL)
@Model(displayName = "Excel表头", summary = "根据排列方向确定表头位置；字段描述中的\"行\"并不仅仅指代\"水平行\"，根据排列方向确定；")
public class ExcelHeaderDefinition extends ExcelRowDefinition {

    private static final long serialVersionUID = -68697537959164386L;

    public static final String MODEL_MODEL = "file.ExcelHeaderDefinition";

    @Field(displayName = "是否配置表头", defaultValue = "false", summary = "配置行不参与计算，且导出时自动忽略；配置行指定的应用范围必须是需要配置的表头范围；")
    private Boolean isConfig;

    @Field(displayName = "是否冻结", defaultValue = "false", summary = "冻结功能仅在非配置行且非隐藏行中生效")
    private Boolean isFrozen;
}
