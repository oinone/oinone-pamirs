package pro.shushi.pamirs.file.api.spi;

import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * Excel国际化初始化API
 *
 * @author Adamancy Zhang at 17:07 on 2024-06-01
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ExcelLocationInitializeService {

    List<ExcelWorkbookDefinition> init(List<ExcelWorkbookDefinition> workbookDefinitions);
}
