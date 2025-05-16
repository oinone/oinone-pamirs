package pro.shushi.pamirs.file.api.entity;

import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.model.ExcelCellRangeDefinition;
import pro.shushi.pamirs.file.api.model.ExcelHeaderDefinition;
import pro.shushi.pamirs.file.api.model.ExcelRowDefinition;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel块定义
 *
 * @author Adamancy Zhang at 11:05 on 2021-08-16
 */
@Data
public class EasyExcelBlockDefinition implements Serializable {

    private static final long serialVersionUID = -5890487002998338465L;

    /**
     * 绑定模型
     */
    private String bindingModel;

    /**
     * 获取函数命名空间
     */
    private String fetchNamespace;

    /**
     * 获取函数名
     */
    private String fetchFun;

    /**
     * 解析类型
     */
    private ExcelAnalysisTypeEnum analysisType;

    /**
     * 排列方向
     */
    private ExcelDirectionEnum direction;

    /**
     * 过滤条件
     */
    private String domain;

    /**
     * 块编号（工作簿唯一，非工作表中的块序列）
     */
    private int blockNumber;

    /**
     * 设计范围
     */
    private ExcelCellRangeDefinition designRange;

    /**
     * 当前范围
     */
    private ExcelCellRangeDefinition currentRange;

    /**
     * 使用css样式合并规则
     */
    private Boolean usingCascadingStyle;

    /**
     * 列属性单元格列表（固定表头）
     */
    private Map<Integer, EasyExcelCellDefinition> columnFieldCells;

    /**
     * 属性单元格列表
     */
    private Map<String, EasyExcelCellDefinition> fieldCells;

    /**
     * 属性的树节点列表
     */
    private List<TreeNode<EasyExcelCellDefinition>> fieldNodeList;

    /**
     * 配置行
     */
    private ExcelHeaderDefinition configHeader;

    /**
     * 表头行
     */
    private List<ExcelHeaderDefinition> headerDefinitionList;

    /**
     * 普通行
     */
    private List<ExcelRowDefinition> rowDefinitionList;

    /**
     * 合并范围列表
     */
    private List<ExcelCellRangeDefinition> mergeRangeList;

    /**
     * 循环合并列表（固定表头）
     */
    private List<EasyExcelLoopMergeDefinition> loopMergeRangeList;

    /**
     * <h>受填充影响需要被平移的块列表（固定表头）</h>
     */
    private List<EasyExcelBlockDefinition> influenceTranslationBlockList;

    /**
     * <h>受填充影响需要被复制填充的块列表（固定表头）</h>
     */
    private List<EasyExcelBlockDefinition> influenceFillBlockList;
}
