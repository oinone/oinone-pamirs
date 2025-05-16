package pro.shushi.pamirs.file.api.easyexcel;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;

/**
 * Excel写处理程序扩展
 *
 * @author Yexiu at 17:47 on 2024/9/4
 */
public interface ExcelWriteHandlerExtendApi extends SheetWriteHandler, RowWriteHandler, CellWriteHandler {

    /**
     * 根据上下文判断是否执行
     *
     * @param context Excel定义上下文
     * @return 是否执行该扩展
     */
    boolean match(ExcelDefinitionContext context);

    default void extendBuilder(ExcelWriterBuilder builder) {
    }
}
