package pro.shushi.pamirs.meta.api.session.cache.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 获取设计器隔离OwnSign
 *
 * @author wx@shushi.pro
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionFillOwnSignApi {

    /**
     * 获取设计器隔离OwnSign
     */
    default String getCdOwnSign() {
        return null;
    }

    /**
     * 获取配置的OwnSign
     */
    default String getConfigCdOwnSign() {
        return null;
    }

    /**
     * 是否处理隔离OwnSign
     */
    default boolean handleOwnSign() {
        return false;
    }

    /**
     * 是否处理缓存同步
     */
    default boolean handleSyncCache() {
        return false;
    }

    /**
     * 二级缓存刷新所有的元数据
     */
    default boolean allMetaRefresh() {
        return false;
    }

    /**
     * 缓存初始化完成
     */
    default void cacheInitCompleted() {
    }
}
