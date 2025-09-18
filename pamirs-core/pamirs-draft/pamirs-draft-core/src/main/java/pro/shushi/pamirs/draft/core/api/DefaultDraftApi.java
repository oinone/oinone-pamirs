package pro.shushi.pamirs.draft.core.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.DraftApi;
import pro.shushi.pamirs.draft.api.model.Draft;
import pro.shushi.pamirs.draft.api.spi.DraftContextApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * @author Gesi at 14:55 on 2025/9/17
 */
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultDraftApi implements DraftApi {

    @Function.fun("queryDraft")
    @Function.Advanced(displayName = "查询草稿", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(summary = "查询草稿", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    @Override
    public <T> T queryDraft(T data) {
        DraftContextApi draftContextApi = getDraftContextApi(data);
        Draft<T> draft = draftContextApi.loadDraftContext(data);
        Draft<T> dbDraft = queryDbDraft(draft);
        if (dbDraft != null) {
            return draftContextApi.deserializationDraftData(dbDraft);
        }
        return null;
    }

    @Function.fun("createOrUpdateDraft")
    @Function.Advanced(displayName = "创建或更新草稿", type = {FunctionTypeEnum.CREATE, FunctionTypeEnum.UPDATE}, managed = true)
    @Function(summary = "创建或更新草稿", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    @Override
    public <T> T createOrUpdateDraft(T data) {
        DraftContextApi draftContextApi = getDraftContextApi(data);
        Draft<T> draft = draftContextApi.loadDraftContext(data);
        Draft<T> dbDraft = queryDbDraft(draft);
        boolean isCreate = true;
        if (dbDraft != null) {
            draft = dbDraft;
            isCreate = false;
        }

        draftContextApi.serializationDraftData(draft, data);

        if (isCreate) {
            draft = draft.create();
        } else {
            draft.updateById();
        }

        return data;
    }

    @Function.fun("deleteDraft")
    @Function.Advanced(displayName = "删除草稿", type = FunctionTypeEnum.DELETE, managed = true)
    @Function(summary = "删除草稿", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    @Override
    public <T> T deleteDraft(T data) {
        DraftContextApi draftContextApi = getDraftContextApi(data);
        Draft<T> draft = draftContextApi.loadDraftContext(data);
        Draft<T> dbDraft = queryDbDraft(draft);
        if (dbDraft == null) {
            return null;
        }
        return Boolean.TRUE.equals(dbDraft.deleteById()) ? data : null;
    }

    private <T> DraftContextApi getDraftContextApi(T data) {
        String model = Models.api().getDataModel(data);
        DraftContextApi draftContextApi = null;
        if (model != null) {
            // 先尝试通过模型获取spi
            try {
                draftContextApi = Spider.getExtension(DraftContextApi.class, model);
            } catch (Exception ignored) {}
        }
        if (draftContextApi == null) {
            draftContextApi = Spider.getExtension(DraftContextApi.class, "defaultDraftContextApi");
        }
        return draftContextApi;
    }

    private <T> Draft<T> queryDbDraft(Draft<T> draft) {
        if (StringUtils.isBlank(draft.getCode())) {
            return null;
        }
        return new Draft<>().queryOneByWrapper(Pops.<Draft<T>>lambdaQuery().from(Draft.MODEL_MODEL).eq(Draft::getCode, draft.getCode()));
    }

}
