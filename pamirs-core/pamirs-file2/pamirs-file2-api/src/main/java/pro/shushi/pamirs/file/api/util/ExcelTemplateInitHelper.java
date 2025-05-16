package pro.shushi.pamirs.file.api.util;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.file.api.spi.ExcelInitializeService;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.Optional;

/**
 * Excel模板初始化
 *
 * @author Adamancy Zhang on 2021-04-14 19:23
 */
public class ExcelTemplateInitHelper {

    public static void init() {
        ExcelInitializeService service = Spider.getDefaultExtension(ExcelInitializeService.class);
        Optional.ofNullable(service.generatorTemplates())
                .filter(CollectionUtils::isNotEmpty)
                .map(service::generatorAfter)
                .filter(CollectionUtils::isNotEmpty)
                .map(service::save)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(service::saveAfter);
    }
}
