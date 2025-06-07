package pro.shushi.pamirs.file.api.context;

import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExcelExportDataHolder {

    private final EasyExcelBlockDefinition blockDefinition;

    private final List<TreeNode<EasyExcelCellDefinition>> fieldList;

    private ExcelExportDataHolder parent = null;

    private final List<Map<String, Object>> resultList;

    private int currentRowIndex = 0;

    private Map<String, Object> currentRow = new HashMap<>();

    private Map<String, Object> needCopyData = new HashMap<>();

    /**
     * 根节点构造
     *
     * @param blockDefinition Excel块定义
     */
    public ExcelExportDataHolder(EasyExcelBlockDefinition blockDefinition) {
        this.blockDefinition = blockDefinition;
        this.fieldList = blockDefinition.getFieldNodeList();
        this.resultList = new ArrayList<>();
    }

    /**
     * 子节点构造
     *
     * @param fieldList 子属性列表
     * @param parent    父节点
     */
    public ExcelExportDataHolder(List<TreeNode<EasyExcelCellDefinition>> fieldList, ExcelExportDataHolder parent) {
        this.fieldList = fieldList;
        this.parent = parent;
        this.blockDefinition = parent.blockDefinition;
        this.resultList = parent.resultList;
        this.currentRow = parent.currentRow;
        this.needCopyData = parent.needCopyData;
    }

    public Map<String, Object> copy() {
        Map<String, Object> newRowData = new HashMap<>();
        for (Map.Entry<String, Object> entry : needCopyData.entrySet()) {
            newRowData.put(entry.getKey(), entry.getValue());
        }
        return newRowData;
    }

    /**
     * 重置迭代器
     */
    public void resetIterator() {
        this.currentRowIndex = 0;
        this.currentRow = this.resultList.get(0);
    }

    /**
     * 选择下一行数据进行处理
     *
     * @return 成功选择已存在的数据行时，返回{@link Boolean#TRUE};否则，返回{@link Boolean#FALSE}
     */
    public boolean nextRow() {
        this.currentRowIndex = this.currentRowIndex + 1;
        if (currentRowIndex < this.resultList.size()) {
            this.currentRow = this.resultList.get(this.currentRowIndex);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 选择一行数据进行处理
     *
     * @param index 数据行的索引
     * @return 成功选择已存在的数据行时，返回{@link Boolean#TRUE};否则，自动扩展结果列表，并选中指定索引所在的数据行，返回{@link Boolean#FALSE}
     */
    public boolean selectRow(int index) {
        this.currentRowIndex = index;
        if (index < this.resultList.size()) {
            this.currentRow = this.resultList.get(index);
            return Boolean.TRUE;
        } else {
            while (index > this.resultList.size()) {
                this.currentRow = new HashMap<>();
                this.resultList.add(this.currentRow);
            }
            this.currentRow = null;
            return Boolean.FALSE;
        }
    }

    public void putCurrentRowValue(String key, Object value) {
        currentRow.put(key, value);
    }

    public void putNeedCopyValue(String key, Object value) {
        needCopyData.put(key, value);
    }
}
