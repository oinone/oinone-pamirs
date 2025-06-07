package pro.shushi.pamirs.file.test4;

import com.alibaba.fastjson.JSONObject;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelLocation;
import pro.shushi.pamirs.file.api.model.ExcelLocationItem;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.test.AbstractExcelTest;

import java.util.*;

/**
 * @author Adamancy Zhang at 17:28 on 2023-09-06
 */
public abstract class AbstractTest4 extends AbstractExcelTest {

    protected static final String TEST_CODE = "123";

    private static final String CURRENT_LANG_CODE = "en-US";

    @Override
    protected ExcelDefinitionContext getDefinitionContext(ExcelWorkbookDefinition workbookDefinition) {
        ExcelDefinitionContext context = super.getDefinitionContext(workbookDefinition);
        context.setCurrentLang(CURRENT_LANG_CODE);
        return context;
    }

    @Override
    protected ExcelWorkbookDefinition mockDefinition() {
        return mockFixedHeader();
    }

    private ExcelWorkbookDefinition mockFixedHeader() {
        ExcelWorkbookDefinition workbookDefinition = WorkbookDefinitionBuilder.newInstance(ExcelImportTask.MODEL_MODEL, TEST_CODE).setName("导入任务模板")
                .createSheet().setName("导入任务")
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_FORMAT, ExcelDirectionEnum.HORIZONTAL, "A1:E1")
                .createMergeRange("B1:E1")
                .createHeader().setIsConfig(Boolean.TRUE)
                .createCell().setAutoSizeColumn(true).and()
                .createCell().setAutoSizeColumn(true).and()
                .createCell().setAutoSizeColumn(true).and()
                .createCell().setAutoSizeColumn(true).and()
                .createCell().setAutoSizeColumn(true).and()
                .and()

                .createRow()
                .createCell().setValue("名称").setStyleBuilder(ExcelHelper.createDefaultStyle()).and()
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER).createTypeface().setSize(26).setBold(true).and()).and()
                .createCell().setStyleBuilder(ExcelHelper.createDefaultStyle()).and()
                .createCell().setStyleBuilder(ExcelHelper.createDefaultStyle()).and()
                .createCell().setStyleBuilder(ExcelHelper.createDefaultStyle()).and()
                .and().and()

                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A2:E3" /* 水平表头，包含填充行 */)
                .setPresetNumber(10)

                //配置行
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("messages[*].level").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSONObject.toJSONString(MapHelper.newInstance(new LinkedHashMap<>(4))
                        .put("TIP", "提示")
                        .put("INFO", "信息")
                        .put("WARNING", "警告")
                        .put("ERROR", "异常")
                        .build())).and()
                .createCell().setField("messages[*].message").setTranslate(Boolean.TRUE).and()
                .createCell().setField("id").and()
                .createCell().setField("model").and()
                .createCell().setField("eachImport").setType(ExcelValueTypeEnum.BOOLEAN).and()

                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle())
                .createCell().setValue("消息级别").and()
                .createCell().setValue("消息内容").and()
                .createCell().setValue("消息ID").and()
                .createCell().setValue("模型编码").and()
                .createCell().setValue("是否逐行导入").and()

                .and().and()
                .and()
                .build();

        List<ExcelLocation> locationList = new ArrayList<>();
        ExcelLocation location = new ExcelLocation();
        location.setLang(CURRENT_LANG_CODE);
        location.setLocationItems(generatorLocationItems());
        locationList.add(location);
        workbookDefinition.setLocations(locationList);
        return workbookDefinition;
    }

    private List<ExcelLocationItem> generatorLocationItems() {
        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("导入任务", "Import Task");
        jsonData.put("名称", "Name");
        jsonData.put("提示", "Tip");
        jsonData.put("信息", "Info");
        jsonData.put("警告", "Warning");
        jsonData.put("异常", "Error");
        jsonData.put("是", "Yes");
        jsonData.put("否", "No");
        jsonData.put("是否逐行导入", "Is Each Import");
        jsonData.put("消息级别", "Message Level");
        jsonData.put("消息内容", "Message Content");
        jsonData.put("消息ID", "Message ID");
        jsonData.put("模型编码", "Model Code");
        jsonData.put("提示1", "Tip1");
        jsonData.put("提示2", "Tip2");
        jsonData.put("提示3", "Tip3");
        List<ExcelLocationItem> locationItems = new ArrayList<>();
        for (Map.Entry<String, String> entry : jsonData.entrySet()) {
            locationItems.add(new ExcelLocationItem().setOrigin(entry.getKey()).setTarget(entry.getValue()));
        }
        return locationItems;
    }
}
