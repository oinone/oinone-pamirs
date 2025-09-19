package pro.shushi.pamirs.draft.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.draft.api.service.DraftService;
import pro.shushi.pamirs.meta.api.core.orm.systems.DraftApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 * @author Gesi at 14:55 on 2025/9/17
 */
@Fun(BaseModel.MODEL_MODEL)
@Component
public class DefaultDraftApi implements DraftApi {

    @Autowired
    private DraftService draftService;

    @Function.Advanced(displayName = "查询草稿", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(summary = "查询草稿", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    @Override
    public <T> T queryDraft(T data) {
        return draftService.queryDraft(data);
    }

    @Function.Advanced(displayName = "创建或更新草稿", type = {FunctionTypeEnum.CREATE, FunctionTypeEnum.UPDATE}, managed = true)
    @Function(summary = "创建或更新草稿", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    @Override
    public <T> T createOrUpdateDraft(T data) {
        return draftService.createOrUpdateDraft(data);
    }

    @Function.Advanced(displayName = "删除草稿", type = FunctionTypeEnum.DELETE, managed = true)
    @Function(summary = "删除草稿", openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    public Boolean deleteDraft(String draftCode) {
        return draftService.deleteDraft(draftCode);
    }

}
