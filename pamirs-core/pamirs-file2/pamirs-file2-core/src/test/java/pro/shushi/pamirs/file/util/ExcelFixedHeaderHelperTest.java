package pro.shushi.pamirs.file.util;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.file.api.enmu.ExcelTemplateTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.util.ExcelHelper;

import java.util.List;

/**
 * @author Adamancy Zhang at 10:53 on 2023-01-05
 */
@DisplayName("ExcelFixedHeaderHelper测试")
public class ExcelFixedHeaderHelperTest {

    protected static final String TEST_CODE = "123";

    @DisplayName("生成单个工作簿")
    @Test
    public void singleSheet() {
        ExcelWorkbookDefinition workbookDefinition = ExcelHelper.fixedHeader(ExcelImportTask.MODEL_MODEL, TEST_CODE)
                .setType(ExcelTemplateTypeEnum.IMPORT)
                .createSheet("sheet1")
                .createBlock(ExcelImportTask.MODEL_MODEL)
                .addUnique(ExcelImportTask.MODEL_MODEL, "code")
                .addColumn("code", new ExcelCellDefinition().setValue("编码").setType(ExcelValueTypeEnum.STRING))
                .addColumn("name", new ExcelCellDefinition().setValue("名称").setType(ExcelValueTypeEnum.STRING))
                .build();
        assertWorkbookDefinition(workbookDefinition, 1);

        System.out.println(JSON.toJSONString(workbookDefinition));
    }


    @DisplayName("生成多个工作簿")
    @Test
    public void multiSheet() {
        ExcelWorkbookDefinition workbookDefinition = ExcelHelper.fixedHeader(ExcelImportTask.MODEL_MODEL, TEST_CODE)
                .setType(ExcelTemplateTypeEnum.IMPORT)
                .createSheet("sheet1")
                .createBlock(ExcelImportTask.MODEL_MODEL)
                .addUnique(ExcelImportTask.MODEL_MODEL, "code")
                .addColumn("code", new ExcelCellDefinition().setValue("编码").setType(ExcelValueTypeEnum.STRING))
                .addColumn("name", new ExcelCellDefinition().setValue("名称").setType(ExcelValueTypeEnum.STRING))
                .createSheet("sheet2")
                .createBlock(ExcelImportTask.MODEL_MODEL)
                .addUnique(ExcelImportTask.MODEL_MODEL, "code")
                .addColumn("code", new ExcelCellDefinition().setValue("编码").setType(ExcelValueTypeEnum.STRING))
                .addColumn("name", new ExcelCellDefinition().setValue("名称").setType(ExcelValueTypeEnum.STRING))
                .build();
        assertWorkbookDefinition(workbookDefinition, 2);

        System.out.println(JSON.toJSONString(workbookDefinition));
    }

    private void assertWorkbookDefinition(ExcelWorkbookDefinition workbookDefinition, int sheetCount) {
        List<ExcelSheetDefinition> sheetDefinitionList = workbookDefinition.getSheetList();
        assert sheetDefinitionList.size() == sheetCount : "工作簿数量错误";

        for (ExcelSheetDefinition sheetDefinition : sheetDefinitionList) {
            assertSheetDefinition(sheetDefinition);
            assertBlockDefinition(sheetDefinition.getBlockDefinitionList().get(0));
        }
    }

    private void assertSheetDefinition(ExcelSheetDefinition sheetDefinition) {
        assert sheetDefinition.getBlockDefinitionList().size() == 1 : "块定义数量错误";
        assert sheetDefinition.getUniqueDefinitions().size() == 1 : "唯一定义数量错误";
    }

    private void assertBlockDefinition(ExcelBlockDefinition blockDefinition) {
        assert blockDefinition.getHeaderList().size() == 2 : "表头定义数量错误";

        ExcelCellRangeDefinition designRange = blockDefinition.getDesignRange();
        assert designRange.getBeginRowIndex() == 0 : "起始行标记错误";
        assert designRange.getBeginColumnIndex() == 0 : "起始列标记错误";
        assert designRange.getEndRowIndex() == 1 : "结束行标记错误";
        assert designRange.getEndColumnIndex() == 1 : "结束列标记错误";
    }
}
