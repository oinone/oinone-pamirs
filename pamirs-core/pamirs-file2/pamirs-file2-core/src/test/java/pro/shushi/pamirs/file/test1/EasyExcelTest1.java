package pro.shushi.pamirs.file.test1;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;

import java.io.IOException;
import java.util.Map;

/**
 * <h>导入/导出功能测试（一期）</h>
 * <p>
 * 1、解析方式支持: 固定表头-水平排列。{@link ExcelAnalysisTypeEnum#FIXED_HEADER}-{@link ExcelDirectionEnum#HORIZONTAL}<br>
 * 2、读取方式支持: 单个sheet中的单个block。<br>
 * 3、合并单元格支持。<br>
 * 4、数据格式支持: <br>
 * - 文本 {@link ExcelValueTypeEnum#STRING}<br>
 * - 整数 {@link ExcelValueTypeEnum#INTEGER}<br>
 * - 数字 {@link ExcelValueTypeEnum#NUMBER}<br>
 * - 日期/时间 {@link ExcelValueTypeEnum#DATETIME}<br>
 * - 公式 {@link ExcelValueTypeEnum#FORMULA} （未完整支持）<br>
 * - 布尔 {@link ExcelValueTypeEnum#BOOLEAN}<br>
 * - 富文本 {@link ExcelValueTypeEnum#RICH_TEXT_STRING}<br>
 * - 枚举 {@link ExcelValueTypeEnum#ENUMERATION}<br>
 * </p>
 *
 * @author Adamancy Zhang at 09:53 on 2021-08-13
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("导入/导出功能测试（一期）")
public class EasyExcelTest1 extends AbstractTest1 {

    @Order(1)
    @DisplayName("生成导入模板测试")
    @Test
    public void testImportTemplate() throws IOException {
        super.testImportTemplate();
    }

    @Order(2)
    @DisplayName("生成导出模板测试")
    @Test
    public void testExportTemplate() throws IOException {
        super.testExportTemplate();
    }

    @Order(3)
    @DisplayName("模板填充测试")
    @Test
    public void fillTemplateData() throws IOException {
        super.fillTemplateData();
    }

    @Order(4)
    @DisplayName("导入模板读取测试")
    @Test
    public void testImportData() {
        super.testImportData();
    }

    @Order(5)
    @DisplayName("导入模板错误收集测试")
    @Test
    public void testErrorImportData() throws IOException {
        super.fillErrorTemplateData();
        super.testErrorImportData(() -> {
            return new TestExcelReadCallback() {
                @Override
                public void process(ExcelImportContext importContext, String modelModel, Map<String, Object> data) {
                    if (StringUtils.isBlank((String) data.get("name"))) {
                        throw new IllegalArgumentException("Invalid task name.");
                    }
                    super.process(importContext, modelModel, data);
                }
            };
        });
    }

    @Override
    protected void appendMockData(ExcelExportContext exportContext) {
        exportContext.addData(mockData());
    }

    @Override
    protected void appendErrorMockData(ExcelExportContext exportContext) {
        exportContext.addData(mockErrorData());
    }
}
