package pro.shushi.pamirs.file.test2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.IndexedColors;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.model.TaskMessage;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.test.AbstractExcelTest;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 导入/导出功能测试模拟数据（二期）
 *
 * @author Adamancy Zhang at 10:33 on 2021-08-13
 */
@SuppressWarnings({"PointlessArithmeticExpression", "ConstantConditions"})
public abstract class AbstractTest2 extends AbstractExcelTest {

    protected static final String TEST_CODE = "123";

    @Override
    protected ExcelWorkbookDefinition mockDefinition() {
//        return mockFixedHeaderAndVerticalDefinition();
        return mockFixedFormatDefinition();
//        return mockFixedHeader();
    }

    private ExcelWorkbookDefinition mockFixedHeaderAndVerticalDefinition() {
        return WorkbookDefinitionBuilder.newInstance(ExcelImportTask.MODEL_MODEL, TEST_CODE).setName("导入任务模板")
                .createSheet().setName("导入任务")
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.VERTICAL, "A1:B7")
                .setPresetNumber(10)
                .createMergeRange("A1:B1")
                .createMergeRange("A3:A5")
                .createMergeRange("A6:A7")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("id").setType(ExcelValueTypeEnum.INTEGER).and()
                .createCell().setField("name").setType(ExcelValueTypeEnum.NUMBER).and()
                .createCell().setField("file.id").and()
                .createCell().setField("file.name").and()
                .createCell().setField("file.url").and()
                .createCell().setField("messages[*].level").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSONObject.toJSONString(MapHelper.newInstance(new LinkedHashMap<>(4))
                        .put("TIP", "提示")
                        .put("INFO", "信息")
                        .put("WARNING", "警告")
                        .put("ERROR", "异常")
                        .build()))
                .and()
                .createCell().setField("messages[*].message").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("ID").and()
                .createCell().setValue("基础信息").and()
                .createCell().setValue("文件信息").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("消息信息").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER).setHeight(2000)).and()
                .createCell().setValue("").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)))
                .createCell().setValue("").and()
                .createCell().setValue("任务名称").and()
                .createCell().setValue("文件ID").and()
                .createCell().setValue("文件名称").and()
                .createCell().setValue("文件链接")
                .setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setSize(18)))
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 2, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setSize(14).build()))
                        .add(new RichTextFormat(2, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).setSize(18).build()))
                        .build())).and()
                .createCell().setValue("消息级别").and()
                .createCell().setValue("消息内容").and()
                .and()
                .and()
                .createUnique(ExcelImportTask.MODEL_MODEL).addUnique("id").and()
                .and()
                .build();
    }

    private ExcelWorkbookDefinition mockFixedFormatDefinition() {
        return WorkbookDefinitionBuilder.newInstance(ExcelImportTask.MODEL_MODEL, TEST_CODE).setName("导入任务模板")
                .createSheet().setName("导入任务").setOnceFetchSheetData(true)
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_FORMAT, ExcelDirectionEnum.HORIZONTAL, "A1:F3")
                .createMergeRange("A1:F1")
                .createMergeRange("D2:F2")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().and()
                .createCell().setAutoSizeColumn(false).and()
                .createCell().and()
                .createCell().setAutoSizeColumn(false).and()
                .createCell().setAutoSizeColumn(false).and()
                .createCell().setAutoSizeColumn(false).and()
                .and()
                .createRow().setStyleBuilder(ExcelHelper.createDefaultStyle())
                .createCell().setValue("标题").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)).and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .and()
                .createRow().setStyleBuilder(ExcelHelper.createDefaultStyle())
                .createCell().setValue("ID").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)).and()
                .createCell().setField("id").setType(ExcelValueTypeEnum.INTEGER).and()
                .createCell().setValue("任务名称").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)).and()
                .createCell().setField("name").setType(ExcelValueTypeEnum.STRING).and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .and()
                .createRow().setStyleBuilder(ExcelHelper.createDefaultStyle())
                .createCell().setValue("文件ID").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)).and()
                .createCell().setField("file.id").setType(ExcelValueTypeEnum.INTEGER).and()
                .createCell().setValue("文件名称").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)).and()
                .createCell().setField("file.name").setType(ExcelValueTypeEnum.STRING).and()
                .createCell().setValue("文件链接").setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)).and()
                .createCell().setField("file.url").and()
                .and().and()
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A4:F5")
                .setPresetNumber(10)
                .createMergeRange("A1:B1") // 相对定位
                .createMergeRange("C1:F1")
                .createMergeRange("A2:B2")
                .createMergeRange("C2:F2")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("messages[*].level").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSONObject.toJSONString(MapHelper.newInstance(new LinkedHashMap<>(4))
                        .put("TIP", "提示")
                        .put("INFO", "信息")
                        .put("WARNING", "警告")
                        .put("ERROR", "异常")
                        .build())).and()
                .createCell().setValue("").and()
                .createCell().setField("messages[*].message").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)
                        .setForegroundColor((int) IndexedColors.GREY_40_PERCENT.getIndex()).setHeight(2000).setWrapText(true))
                .createCell().setValue("消息级别消息级别消息级别消息级别消息级别消息级别消息级别").and()
                .createCell().setValue("").and()
                .createCell().setValue("消息内容").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .and().and()
                .and()
                .build();
    }

    private ExcelWorkbookDefinition mockFixedHeader() {
        return WorkbookDefinitionBuilder.newInstance(ExcelImportTask.MODEL_MODEL, TEST_CODE).setName("导入任务模板")
                .createSheet().setName("导入任务")
                .createMergeRange("A3:G3")
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:G8")
                .setPresetNumber(10)
                .createMergeRange("A6:B6").createMergeRange("C6:F6")
                .createMergeRange("A8:B8").createMergeRange("C8:F8") // 这两个合并单元格会随着填充的值自动向下推移
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("messages[*].level").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSONObject.toJSONString(MapHelper.newInstance(new LinkedHashMap<>(4))
                        .put("TIP", "提示")
                        .put("INFO", "信息")
                        .put("WARNING", "警告")
                        .put("ERROR", "异常")
                        .build())).setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setValue("").and()
                .createCell().setField("messages[*].message")
                .and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setField("messages[*].recordDate").setType(ExcelValueTypeEnum.DATETIME).setFormat("yyyy-MM-dd").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .and()
                .createHeader()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .createCell().setValue("").createStyle().and().and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(true).setSize(28)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("这是一个长长的标题").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .and()
                .createHeader()
                .createCell().setValue("1").createStyle().and().and()
                .createCell().setValue("1").createStyle().and().and()
                .createCell().setValue("1").createStyle().and().and()
                .createCell().setValue("1").createStyle().and().and()
                .createCell().setValue("1").createStyle().and().and()
                .createCell().setValue("1").createStyle().and().and()
                .createCell().setValue("1").createStyle().and().and()
                .and()
                .createHeader()
                .createCell().setValue("a").createStyle().and().and()
                .createCell().setValue("b").createStyle().and().and()
                .createCell().setValue("c").createStyle().and().and()
                .createCell().setValue("d").createStyle().and().and()
                .createCell().setValue("e").createStyle().and().and()
                .createCell().setValue("f").createStyle().and().and()
                .createCell().setValue("g").createStyle().and().and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER)
                        .setForegroundColor((int) IndexedColors.GREY_40_PERCENT.getIndex()).setHeight(2000).setWrapText(true))
                .createCell().setValue("消息级别消息级别消息级别消息级别消息级别消息级别消息级别").and()
                .createCell().setValue("").and()
                .createCell().setValue("消息内容").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("").and()
                .createCell().setValue("记录时间").and()
                .and()
                .createHeader()
                .createCell().setValue("a").createStyle().and().and()
                .createCell().setValue("b").createStyle().and().and()
                .createCell().setValue("c").createStyle().and().and()
                .createCell().setValue("d").createStyle().and().and()
                .createCell().setValue("e").createStyle().and().and()
                .createCell().setValue("f").createStyle().and().and()
                .createCell().setValue("g").createStyle().and().and()
                .and()
                .and()
                .and()
                .build();
    }

    protected ExcelImportTask mockOneData() {
        return (ExcelImportTask) new ExcelImportTask()
                .setFile((PamirsFile) new PamirsFile().setName("导入文件3").setUrl("url").setId(13L))
                .setName("111111111111.11")
                .setMessages(CollectionHelper.<TaskMessage>newInstance()
                        .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示1").setRecordDate(new Date()))
                        .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示2").setRecordDate(new Date()))
                        .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示3").setRecordDate(new Date()))
                        .build())
                .setId(1111111111111L);
    }

    protected List<ExcelImportTask> mockData() {
        return CollectionHelper.<ExcelImportTask>newInstance()
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件1").setId(1111111111111111111L))
                        .setName("111111111.11")
                        .setMessages(CollectionHelper.<TaskMessage>newInstance()
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示1"))
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示2"))
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示3"))
                                .build())
                        .setId(Long.MAX_VALUE))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件2").setUrl("url").setId(12L))
                        .setName("11111111111.11")
                        .setMessages(CollectionHelper.<TaskMessage>newInstance()
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示1"))
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示2"))
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示3"))
                                .build())
                        .setId(Long.MIN_VALUE))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件3").setUrl("url").setId(13L))
                        .setName("111111111111.11")
                        .setMessages(CollectionHelper.<TaskMessage>newInstance()
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示1"))
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示2"))
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示3"))
                                .build())
                        .setId(1111111111111L))
                .build();
    }
}
