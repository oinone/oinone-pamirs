package pro.shushi.pamirs.draft.api.spi;

import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 草稿存储策略API
 *
 * @author Gesi at 17:55 on 2025/9/19
 */
@SPI
public interface DraftStoreStrategyApi {

    Draft queryDraft(Draft draft);

    Draft createOrUpdateDraft(Draft draft);

    Boolean deleteDraft(String draftCode);

}
