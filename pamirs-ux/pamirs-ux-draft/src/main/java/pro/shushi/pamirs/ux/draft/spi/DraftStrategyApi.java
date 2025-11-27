package pro.shushi.pamirs.ux.draft.spi;

import pro.shushi.pamirs.ux.draft.config.DraftConfigure;
import pro.shushi.pamirs.ux.draft.model.Draft;
import pro.shushi.pamirs.ux.draft.session.DraftSessionContext;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.ux.common.entity.HoldSupplier;

/**
 * 草稿存储策略API
 *
 * @author Gesi at 17:55 on 2025/9/19
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DraftStrategyApi {

    DraftSessionContext loadSession(Function function, Object... args);

    Draft load(String draftCode, Object data);

    Draft load(Object data);

    Draft get(Object data);

    Draft getByWrapper(IWrapper<?> wrapper);

    Draft create(Draft draft);

    Draft update(Draft draft);

    boolean delete(String draftCode);

    <T> Object serialization(String model, T data);

    <T> T deserialization(String model, Object draftData);

    HoldSupplier<DraftStrategyApi> HOLDER = new HoldSupplier<>(() -> Spider.getExtension(DraftStrategyApi.class, DraftConfigure.getStorage()));
}
