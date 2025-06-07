package pro.shushi.pamirs.file.api.context;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.model.ExcelSheetDefinition;
import pro.shushi.pamirs.meta.annotation.fun.Data;

@Data
public class ExcelProcessContext {

    private ExcelDefinitionContext definitionContext;

    private ExcelSheetDefinition originSheetDefinition;

    private EasyExcelSheetDefinition sheetDefinition;

    private Workbook workbook;

    private Sheet sheet;
}
