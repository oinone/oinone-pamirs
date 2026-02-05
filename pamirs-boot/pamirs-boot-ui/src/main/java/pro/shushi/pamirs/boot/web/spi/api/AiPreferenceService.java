package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.base.model.View;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * AiPreferenceService
 *
 * @author yakir on 2026/02/05 14:12.
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AiPreferenceService {

    String load(ViewAction viewAction, View view);

}
