package pro.shushi.pamirs.boot.web.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.Draft;
import pro.shushi.pamirs.boot.web.enmu.DraftExpEnumerate;
import pro.shushi.pamirs.boot.web.service.DraftService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;

/**
 *
 * @author Gesi at 10:34 on 2025/9/1
 */
@Service
public class DraftServiceImpl implements DraftService {

    @Override
    public Draft createOrUpdateDraft(Draft draft) {
        if (draft == null || StringUtils.isBlank(draft.getViewIdentifier())) {
            throw PamirsException.construct(DraftExpEnumerate.DRAFT_VIEW_IDENTIFIER_NULL_ERROR).errThrow();
        }
        if (PamirsSession.getUserId() == null) {
            return new Draft();
        }
        draft.setUserId(PamirsSession.getUserId());
        if (draft.getId() == null) {
            draft = draft.create();
            draft = draft.queryById();
        } else {
            draft.updateById();
        }
        return draft;
    }

    @Override
    public Draft queryDraft(String viewIdentifier) {
        if (StringUtils.isBlank(viewIdentifier)) {
            throw PamirsException.construct(DraftExpEnumerate.DRAFT_VIEW_IDENTIFIER_NULL_ERROR).errThrow();
        }
        if (PamirsSession.getUserId() == null) {
            return new Draft().setHasDraft(false);
        }
        List<Draft> draftList = new Draft().queryList(
                Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL)
                        .eq(Draft::getViewIdentifier, viewIdentifier)
                        .eq(Draft::getUserId, PamirsSession.getUserId())
                        .orderByDesc(Draft::getWriteDate)
        );
        if (CollectionUtils.isEmpty(draftList)) {
            return new Draft().setHasDraft(false);
        }
        return draftList.get(0).setHasDraft(true);
    }

    @Override
    public Draft deleteDraft(String viewIdentifier) {
        if (StringUtils.isBlank(viewIdentifier)) {
            throw PamirsException.construct(DraftExpEnumerate.DRAFT_VIEW_IDENTIFIER_NULL_ERROR).errThrow();
        }
        if (PamirsSession.getUserId() == null) {
            return new Draft();
        }
        Integer deleted = new Draft().deleteByWrapper(
                Pops.<Draft>lambdaQuery().from(Draft.MODEL_MODEL)
                        .eq(Draft::getViewIdentifier, viewIdentifier)
                        .eq(Draft::getUserId, PamirsSession.getUserId())
        );
        return new Draft().setHasDraft(deleted > 0);
    }
}
