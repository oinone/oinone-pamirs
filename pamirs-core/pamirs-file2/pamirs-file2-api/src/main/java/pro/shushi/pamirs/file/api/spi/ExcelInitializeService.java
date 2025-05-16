package pro.shushi.pamirs.file.api.spi;

import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * Excel初始化服务
 *
 * @author Adamancy Zhang at 16:06 on 2024-06-01
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExcelInitializeService {

    List<ExcelWorkbookDefinition> generatorTemplates();

    List<ExcelWorkbookDefinition> generatorAfter(List<ExcelWorkbookDefinition> workbookDefinitions);

    List<ExcelWorkbookDefinition> save(List<ExcelWorkbookDefinition> workbookDefinitions);

    void saveAfter(List<ExcelWorkbookDefinition> workbookDefinitions);
}
