package pro.shushi.pamirs.file.api.easyexcel;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Excel解析事件监听
 *
 * @author Adamancy Zhang
 * @date 2020-11-27 16:42
 */
public interface ExcelAnalysisEventListener {

    /**
     * 导入上下文
     *
     * @return 导入上下文
     */
    ExcelImportContext getImportContext();

    /**
     * 当前Excel工作表
     *
     * @return 当前Excel工作表
     */
    EasyExcelSheetDefinition getCurrentSheet();

    /**
     * 当前行所在块列表
     *
     * @return 当前行所在块列表
     */
    List<EasyExcelBlockDefinition> getCurrentBlocks();

    /**
     * 当前块（可能为空）
     *
     * @return 当前块
     */
    @Nullable
    EasyExcelBlockDefinition getCurrentBlock();

    /**
     * 当前所有块缓存数据
     *
     * @return 当前所有块缓存数据
     */
    Map<Integer, Map<String, Object>> getCurrentBlockData();

    /**
     * 当前所有块缓存的读取队列
     *
     * @return 当前所有块缓存的读取队列
     */
    Map<Integer, Queue<Map<Integer, String>>> getCurrentBlockQueue();

    /**
     * 监听回调
     *
     * @return 监听回调
     */
    ExcelReadCallback getCallback();

    /**
     * 是否有下一行
     *
     * @return 有下一行
     */
    boolean hasNext();

    /**
     * 中断
     */
    void interrupt();

    /**
     * 单行数据执行
     *
     * @param rowIndex 行索引
     * @param data     单行数据
     */
    void invoke(int rowIndex, Map<Integer, String> data);
}
