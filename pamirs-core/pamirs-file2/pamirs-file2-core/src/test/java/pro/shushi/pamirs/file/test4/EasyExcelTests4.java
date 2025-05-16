package pro.shushi.pamirs.file.test4;

import org.junit.jupiter.api.*;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.file.api.context.ExcelExportContext;
import pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.TaskMessage;
import pro.shushi.pamirs.meta.common.util.UUIDUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h>导入/导出功能测试（四期）</h>
 * <p>
 * 注: 以下内容均为四期的扩展说明。<br>
 * 1、新增模板国际化支持
 * </p>
 *
 * @author Adamancy Zhang at 09:52 on 2021-08-13
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("导入/导出功能测试（四期）")
public class EasyExcelTests4 extends AbstractTest4 {

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

    @Override
    protected void appendMockData(ExcelExportContext exportContext) {
        exportContext.addData(mockOneData())
                .addData(mockOneData());
    }

    protected ExcelImportTask mockOneData1(String name) {
        return (ExcelImportTask) new ExcelImportTask()
                .setFile((PamirsFile) new PamirsFile().setName(UUIDUtil.getUUIDNumberString()).setUrl("url").setId(13L))
                .setName(name)
                .setId(1111111111111L);
    }

    protected ExcelImportTask mockOneData2(String messagePrefix, int loop) {
        List<TaskMessage> messages = new ArrayList<>();
        for (int i = 1; i <= loop; i++) {
            messages.add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage(messagePrefix + i).setRecordDate(new Date()));
        }
        return (ExcelImportTask) new ExcelImportTask().setMessages(messages);
    }

    protected ExcelImportTask mockOneData() {
        return (ExcelImportTask) new ExcelImportTask()
                .setEachImport(Boolean.TRUE)
                .setFile((PamirsFile) new PamirsFile().setName("导入文件3").setUrl("url").setId(13L))
                .setName("111111111111.11")
                .setModel("aaaa")
                .setMessages(CollectionHelper.<TaskMessage>newInstance()
                        .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示1").setRecordDate(new Date()))
                        .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示2").setRecordDate(new Date()))
                        .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示3").setRecordDate(new Date()))
                        .build())
                .setId(1111111111111L);
    }
}
