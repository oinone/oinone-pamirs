package pro.shushi.pamirs.draft.core.spi;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.draft.api.spi.DraftStoreStrategyApi;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 数据库草稿存储策略API
 * @author Gesi at 17:56 on 2025/9/19
 */
@Component
@SPI.Service
public class DraftDBStoreStrategyApi implements DraftStoreStrategyApi {

    @Override
    public Draft queryDraft(Draft draft) {
        return new Draft().queryOneByWrapper(Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL).eq(Draft::getCode, draft.getCode()));
    }

    @Override
    public Draft createOrUpdateDraft(Draft draft) {
        draft.createOrUpdate();
        return draft;
    }

    @Override
    public Boolean deleteDraft(String draftCode) {
        Integer deleted = new Draft().deleteByWrapper(
                Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL)
                        .eq(Draft::getCode, draftCode)
        );
        return deleted > 0;
    }
}
