package pro.shushi.pamirs.draft.api.service;

/**
 * @author Gesi at 16:33 on 2025/9/19
 */
public interface DraftService {

    <T> T queryDraft(T data);

    <T> T createOrUpdateDraft(T data);

    Boolean deleteDraft(String draftCode);

}
