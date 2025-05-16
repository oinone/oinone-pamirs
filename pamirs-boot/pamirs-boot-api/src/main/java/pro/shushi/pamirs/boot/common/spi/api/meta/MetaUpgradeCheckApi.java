package pro.shushi.pamirs.boot.common.spi.api.meta;

import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 元数据升级检查API
 *
 * @author Adamancy Zhang at 20:16 on 2024-07-23
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaUpgradeCheckApi {

    String DELETE_OPERATOR = "删除";

    String UPDATE_OPERATOR = "变更";

    boolean isAllFlush();

    void saveMetadataToDB(String model);

    void saveMetadataToSession(String model, String operator);

    void saveMetadataFinished();

    HoldKeeper<MetaUpgradeCheckApi> holder = new HoldKeeper<>();

    static MetaUpgradeCheckApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(MetaUpgradeCheckApi.class));
    }
}
