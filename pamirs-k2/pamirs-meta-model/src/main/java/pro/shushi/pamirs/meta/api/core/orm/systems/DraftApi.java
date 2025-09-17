package pro.shushi.pamirs.meta.api.core.orm.systems;

/**
 * 草稿API
 *
 * @author Gesi at 14:52 on 2025/9/17
 */
public interface DraftApi {

    <T> T queryDraft(T data);

    <T> T createOrUpdateDraft(T data);

    <T> T deleteDraft(T data);

}
