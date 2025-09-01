package pro.shushi.pamirs.boot.web.service;

import pro.shushi.pamirs.boot.base.model.Draft;

/**
 * @author Gesi at 10:33 on 2025/9/1
 */
public interface DraftService {

    Draft createOrUpdateDraft(Draft draft);

    Draft queryDraft(String viewIdentifier);

    Draft deleteDraft(String viewIdentifier);

}
