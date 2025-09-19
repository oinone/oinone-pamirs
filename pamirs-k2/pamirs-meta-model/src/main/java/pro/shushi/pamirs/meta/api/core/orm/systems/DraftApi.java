package pro.shushi.pamirs.meta.api.core.orm.systems;

import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

/**
 * 草稿API
 *
 * @author Gesi at 14:52 on 2025/9/17
 */
public interface DraftApi {

    <T> T queryDraft(T data);

    <T> T queryDraftByWrapper(IWrapper<T> queryWrapper);

    <T> T createDraft(T data);

    <T> T updateDraft(T data);

    Boolean deleteDraft(String draftCode);

}
