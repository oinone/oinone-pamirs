package pro.shushi.pamirs.file.test2;

import org.junit.jupiter.api.*;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;

import java.io.IOException;

/**
 * <h>导入/导出功能测试（二期）</h>
 * <p>
 * 注: 以下内容均为二期的扩展说明。<br>
 * 1、解析方式支持: 固定格式。固定格式无排列方向。{@link ExcelAnalysisTypeEnum#FIXED_FORMAT}
 * 2、读取方式支持: 单个sheet中的多个block。<br>
 * 3、多block模式下，默认根据block数量执行回调，无法一次获取全部block数据。如需一次获取，可使用<br>
 * </p>
 *
 * @author Adamancy Zhang at 09:52 on 2021-08-13
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("导入/导出功能测试（二期）")
public class EasyExcelTests2 extends AbstractTest2 {

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
    @DisplayName("导入模板读取测试")
    @Test
    public void testErrorImportData() {
        super.testImportData();
    }

    @Override
    protected void appendMockData(ExcelExportContext exportContext) {
        exportContext.addData(mockOneData())
                .addData(mockOneData());
    }
}
