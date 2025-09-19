package pro.shushi.pamirs.draft.api.service;

import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * @author Gesi at 16:33 on 2025/9/19
 */
@Fun(Draft.MODEL_MODEL)
public interface DraftService {

    <T> T queryDraft(T data);

    <T> T queryDraftByWrapper(IWrapper<T> queryWrapper);

    <T> T createDraft(T data);

    <T> T updateDraft(T data);

    Boolean deleteDraft(String draftCode);

}
