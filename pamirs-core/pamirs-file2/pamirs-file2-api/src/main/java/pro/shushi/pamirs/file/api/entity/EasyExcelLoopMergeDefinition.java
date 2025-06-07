package pro.shushi.pamirs.file.api.entity;

import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

/**
 * EasyExcel循环合并定义
 *
 * @author Adamancy Zhang at 13:08 on 2021-08-16
 */
@Data
public class EasyExcelLoopMergeDefinition implements Serializable {

    private static final long serialVersionUID = 6433694726242253512L;

    /**
     * 左上角单元格样式
     */
    private ExcelStyleDefinition firstStyle;

    /**
     * 合并范围
     */
    private ExcelCellRangeDefinition mergeRange;
}
