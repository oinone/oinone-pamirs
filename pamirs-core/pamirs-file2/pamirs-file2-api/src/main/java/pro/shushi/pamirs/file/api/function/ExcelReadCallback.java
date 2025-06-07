package pro.shushi.pamirs.file.api.function;

import pro.shushi.pamirs.file.api.context.ExcelImportContext;

import java.util.Map;

/**
 * Excel读取回调
 *
 * @author Adamancy Zhang at 16:05 on 2021-08-17
 */
@FunctionalInterface
public interface ExcelReadCallback {

    /**
     * 回调处理
     *
     * @param importContext 导入上下文
     * @param modelModel    模型编码
     * @param data          模型数据
     */
    void process(ExcelImportContext importContext, String modelModel, Map<String, Object> data);
}
