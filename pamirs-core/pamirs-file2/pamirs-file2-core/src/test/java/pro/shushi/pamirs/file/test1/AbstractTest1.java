package pro.shushi.pamirs.file.test1;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 导入/导出功能测试模拟数据（一期）
 *
 * @author Adamancy Zhang at 10:34 on 2021-08-13
 */
@SuppressWarnings("PointlessArithmeticExpression")
public abstract class AbstractTest1 extends AbstractExcelTest {

    protected static final String TEST_CODE = "123";

    @Override
    protected ExcelWorkbookDefinition mockDefinition() {
        int baseRow = 3, baseColumn = 4;
        return WorkbookDefinitionBuilder.newInstance(ExcelImportTask.MODEL_MODEL, TEST_CODE).setName("导入任务模板")
                .createSheet().setName("导入任务")
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        baseRow + 0, baseRow + 2, baseColumn + 0, baseColumn + 6)
                .setPresetNumber(10)
                .createMergeRange(0, 1, 0, 0)
                .createMergeRange(0, 0, 2, 4)
                .createMergeRange(0, 0, 5, 6)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("id").setType(ExcelValueTypeEnum.INTEGER).and()
                .createCell().setField("name").and()
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
                .createCell().setValue("消息信息").and()
                .createCell().setValue("").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("").and()
                .createCell().setValue("任务名称").and()
                .createCell().setValue("文件ID").and()
                .createCell().setValue("文件名称").and()
                .createCell().setValue("文件链接")
                .setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setSize(18)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
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

    protected List<ExcelImportTask> mockErrorData() {
        return mockErrorData1();
//        return mockErrorData2();
    }

    protected List<ExcelImportTask> mockErrorData1() {
        return CollectionHelper.<ExcelImportTask>newInstance()
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件1").setId(1111111111111111111L))
                        .setName("111111111.11")
                        .setId(Long.MAX_VALUE))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件2").setUrl("url").setId(12L))
//                        .setName("11111111111.11")
                        .setId(Long.MIN_VALUE))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件3").setUrl("url").setId(13L))
//                        .setName("111111111111.11")
                        .setId(1111111111111L))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件4").setUrl("url").setId(14L))
                        .setName("11111111.11")
                        .setMessages(CollectionHelper.<TaskMessage>newInstance()
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示1"))
                                .build())
                        .setId(1111111111L))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件5").setUrl("url").setId(15L))
                        .setName("111111111111.11")
                        .setMessages(CollectionHelper.<TaskMessage>newInstance()
                                .add(new TaskMessage().setLevel(TaskMessageLevelEnum.INFO).setMessage("提示2"))
                                .build())
                        .setId(1111111111L))
                .build();
    }

    protected List<ExcelImportTask> mockErrorData2() {
        return CollectionHelper.<ExcelImportTask>newInstance()
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件1").setId(1111111111111111111L))
                        .setName("111111111.11")
                        .setId(Long.MAX_VALUE))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件2").setUrl("url").setId(12L))
//                        .setName("11111111111.11")
                        .setId(Long.MIN_VALUE))
                .add((ExcelImportTask) new ExcelImportTask()
                        .setFile((PamirsFile) new PamirsFile().setName("导入文件3").setUrl("url").setId(13L))
//                        .setName("111111111111.11")
                        .setId(1111111111111L))
                .build();
    }
}
