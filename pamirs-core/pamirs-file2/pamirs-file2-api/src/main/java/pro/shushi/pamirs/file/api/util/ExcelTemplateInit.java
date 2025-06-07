package pro.shushi.pamirs.file.api.util;

import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;

import java.util.List;

/**
 * Excel模板初始化
 *
 * @author Adamancy Zhang on 2021-04-14 19:24
 */
public interface ExcelTemplateInit {

    /**
     * 生成Excel工作簿
     *
     * @return Excel工作簿
     */
    List<ExcelWorkbookDefinition> generator();
}
