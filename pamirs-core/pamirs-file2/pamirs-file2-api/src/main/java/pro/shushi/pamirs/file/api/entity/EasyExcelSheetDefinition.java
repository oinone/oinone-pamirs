package pro.shushi.pamirs.file.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelStyleDefinition;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EasyExcel工作表定义
 *
 * @author Adamancy Zhang at 15:58 on 2021-08-16
 */
@Data
public class EasyExcelSheetDefinition implements Serializable {

    private static final long serialVersionUID = -44455163225940316L;

    /**
     * 工作表名称
     */
    private String name;

    /**
     * 自动列宽
     */
    private Boolean autoSizeColumn;

    /**
     * 一次性拿取工作表数据（该属性仅在包含【固定格式】块时生效，并且所有块的绑定模型必须一致）
     */
    private Boolean onceFetchData;

    /**
     * EasyExcel块定义列表
     */
    private List<EasyExcelBlockDefinition> blockDefinitions;

    /**
     * 列样式 columnIndex -> {@link ExcelStyleDefinition}
     */
    private Map<Integer, ExcelStyleDefinition> columnStyles;

    /**
     * 真实合并范围列表
     */
    private List<ExcelCellRangeDefinition> mergeRangeList;

    /**
     * 真实循环合并范围列表
     */
    private List<EasyExcelLoopMergeDefinition> loopMergeRangeList;

    /**
     * 唯一键定义 model -> uniqueKey1,uniqueKey2...
     */
    private Map<String, Set<String>> uniqueDefinitions;

    /**
     * 工作表最大设计范围
     */
    private ExcelCellRangeDefinition designRange;

    /**
     * 工作表当前范围
     */
    @JSONField(serialize = false)
    private ExcelCellRangeDefinition currentRange;

    @JSONField(serialize = false)
    private Workbook workbook;

    @JSONField(serialize = false)
    private Sheet sheet;
}
