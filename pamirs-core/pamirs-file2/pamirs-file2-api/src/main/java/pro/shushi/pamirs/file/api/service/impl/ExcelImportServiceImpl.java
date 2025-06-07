package pro.shushi.pamirs.file.api.service.impl;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.function.lambda.PamirsSupplier;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.service.ExcelImportService;

/**
 * Excel导入服务
 *
 * @author Adamancy Zhang at 18:05 on 2024-03-28
 */
@Service(ExcelImportServiceImpl.BEAN_NAME)
public class ExcelImportServiceImpl implements ExcelImportService {

    public static final String BEAN_NAME = "excelImportServiceImpl";

    @Override
    public boolean doImport(ExcelImportTask importTask, ExcelDefinitionContext context, PamirsSupplier<ExcelReadCallback> callbackSupplier) {
        return false;
    }

}
