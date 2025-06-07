package pro.shushi.pamirs.core.common.spi;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.core.common.entry.TopBarAction;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

/**
 * 顶部栏扩展API
 *
 * @author Adamancy Zhang at 20:24 on 2024-02-28
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TopBarActionExtendApi {

    default void edit(List<TopBarAction> list) {
    }

    default void fill(Action action, TopBarAction topBarAction) {
    }
}
