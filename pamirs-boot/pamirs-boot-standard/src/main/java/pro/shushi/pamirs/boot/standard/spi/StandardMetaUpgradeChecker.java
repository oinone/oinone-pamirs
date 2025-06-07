package pro.shushi.pamirs.boot.standard.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaUpgradeCheckApi;
import pro.shushi.pamirs.boot.standard.service.MetadataUpgradeChecker;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 标准元数据升级检查API
 *
 * @author Adamancy Zhang at 20:18 on 2024-07-23
 */
@Order(66)
@Component
@SPI.Service
public class StandardMetaUpgradeChecker implements MetaUpgradeCheckApi {

    @Override
    public boolean isAllFlush() {
        return MetadataUpgradeChecker.isAllFlush();
    }

    @Override
    public void saveMetadataToDB(String model) {
        MetadataUpgradeChecker.saveMetadataToDB(model);
    }

    @Override
    public void saveMetadataToSession(String model, String operator) {
        MetadataUpgradeChecker.saveMetadataToSession(model, operator);
    }

    @Override
    public void saveMetadataFinished() {
        MetadataUpgradeChecker.saveMetadataFinished();
    }
}
