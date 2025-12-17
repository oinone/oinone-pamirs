package pro.shushi.pamirs.ux.draft.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.ux.draft.constant.DraftConstants;
import pro.shushi.pamirs.ux.draft.enums.DraftExpEnumerate;
import pro.shushi.pamirs.ux.draft.model.Draft;
import pro.shushi.pamirs.ux.draft.spi.DraftStrategyApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

/**
 * 默认草稿API
 *
 * @author Gesi at 14:55 on 2025/9/17
 */
@Base
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultDraftApi {

    @Function.Advanced(displayName = "查询草稿", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public <T> T queryDraft(T data) {
        DraftStrategyApi api = DraftStrategyApi.HOLDER.get();
        Draft draft = api.get(data);
        if (draft == null) {
            return null;
        }
        verify(draft);
        data = api.deserialization(draft.getModel(), draft.getDraftData());
        FieldUtils.setFieldValue(data, DraftConstants.DRAFT_CODE_FILED, draft.getCode());
        return data;
    }

    @Function.Advanced(displayName = "查询草稿", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public <T> T queryDraftByWrapper(IWrapper<T> queryWrapper) {
        DraftStrategyApi api = DraftStrategyApi.HOLDER.get();
        Draft draft = api.getByWrapper(queryWrapper);
        if (draft == null) {
            return null;
        }
        verify(draft);
        T data = api.deserialization(draft.getModel(), draft.getDraftData());
        FieldUtils.setFieldValue(data, DraftConstants.DRAFT_CODE_FILED, draft.getCode());
        return data;
    }

    @Function.Advanced(displayName = "创建草稿", type = FunctionTypeEnum.CREATE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public <T> T createDraft(T data) {
        DraftStrategyApi api = DraftStrategyApi.HOLDER.get();
        Draft draft = api.load(data);
        if (draft == null) {
            return null;
        }
        verify(draft);
        draft.setDraftData(api.serialization(draft.getModel(), data));
        api.create(draft);
        FieldUtils.setFieldValue(data, DraftConstants.DRAFT_CODE_FILED, draft.getCode());
        return data;
    }

    @Function.Advanced(displayName = "更新草稿", type = FunctionTypeEnum.UPDATE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public <T> T updateDraft(T data) {
        DraftStrategyApi api = DraftStrategyApi.HOLDER.get();
        Draft draft = api.load(data);
        if (draft == null) {
            return null;
        }
        verify(draft);
        draft.setDraftData(api.serialization(draft.getModel(), data));
        api.update(draft);
        FieldUtils.setFieldValue(data, DraftConstants.DRAFT_CODE_FILED, draft.getCode());
        return data;
    }

    @Function.Advanced(displayName = "删除草稿", type = FunctionTypeEnum.DELETE, managed = true)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public Boolean deleteDraft(String draftCode) {
        if (StringUtils.isBlank(draftCode)) {
            throw PamirsException.construct(DraftExpEnumerate.DRAFT_CODE_IS_NULL).errThrow();
        }
        return DraftStrategyApi.HOLDER.get().delete(draftCode);
    }

    private void verify(Draft draft) {
        String code = draft.getCode();
        if (StringUtils.isBlank(code)) {
            throw PamirsException.construct(DraftExpEnumerate.DRAFT_CODE_IS_NULL).errThrow();
        }
        String model = draft.getModel();
        if (StringUtils.isBlank(model)) {
            throw PamirsException.construct(DraftExpEnumerate.DRAFT_MODEL_IS_NULL).errThrow();
        }
    }
}
