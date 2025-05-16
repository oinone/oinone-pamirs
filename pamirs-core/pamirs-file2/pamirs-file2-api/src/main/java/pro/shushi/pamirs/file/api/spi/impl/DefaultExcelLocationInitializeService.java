package pro.shushi.pamirs.file.api.spi.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.spi.ExcelLocationInitializeService;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 默认Excel国际化配置初始化服务
 *
 * @author Adamancy Zhang at 12:05 on 2024-06-03
 */
@Order
@Component
@SPI.Service
public class DefaultExcelLocationInitializeService implements ExcelLocationInitializeService {

    @Override
    public List<ExcelWorkbookDefinition> init(List<ExcelWorkbookDefinition> workbookDefinitions) {
        return workbookDefinitions;
    }
}
