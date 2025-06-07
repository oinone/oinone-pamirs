package pro.shushi.pamirs.file.test3;

import com.alibaba.fastjson.JSONObject;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelHorizontalAlignmentEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.test.AbstractExcelTest;

import java.util.LinkedHashMap;

/**
 * @author Adamancy Zhang at 17:28 on 2023-09-06
 */
public abstract class AbstractTest3 extends AbstractExcelTest {

    protected static final String TEST_CODE = "123";

    @Override
    protected ExcelWorkbookDefinition mockDefinition() {
        return mockFixedHeader();
    }

    private ExcelWorkbookDefinition mockFixedHeader() {
        return WorkbookDefinitionBuilder.newInstance(ExcelImportTask.MODEL_MODEL, TEST_CODE).setName("导入任务模板")
                .createSheet().setName("导入任务")
                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_FORMAT, ExcelDirectionEnum.HORIZONTAL, "A1:D1")
                .createMergeRange("B1:D1")
                .createHeader().setIsConfig(Boolean.TRUE)
                .createCell().setAutoSizeColumn(false).and()
                .createCell().setAutoSizeColumn(true).and()
                .createCell().and()
                .createCell().and().and()

                .createRow()
                .createCell().setValue("名称").setStyleBuilder(ExcelHelper.createDefaultStyle()).and()
                .createCell().setField("name").createStyle().setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER).createTypeface().setSize(26).setBold(true).and().and().and()
                .createCell().and()
                .createCell().and()
                .and().and()

                .createBlock(ExcelImportTask.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A2:D3"/**水平表头，包含填充行*/)
                .setPresetNumber(10) //???

                //配置行
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("messages[*].level").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSONObject.toJSONString(MapHelper.newInstance(new LinkedHashMap<>(4))
                        .put("TIP", "提示")
                        .put("INFO", "信息")
                        .put("WARNING", "警告")
                        .put("ERROR", "异常")
                        .build())).and()
                .createCell().setField("messages[*].message").and()
                .createCell().setField("id").and()
                .createCell().setField("model").and()

                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle())
                .createCell().setValue("消息级别").and()
                .createCell().setValue("消息内容").and()
                .createCell().setValue("消息ID").and()
                .createCell().setValue("模型编码").and()

                .and().and()
                .and()
                .build();
    }
}
