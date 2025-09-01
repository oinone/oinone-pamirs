package pro.shushi.pamirs.boot.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.Draft;
import pro.shushi.pamirs.boot.web.service.DraftService;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

/**
 *
 * @author Gesi at 10:27 on 2025/9/1
 */
@Base
@Component
@Model.model(Draft.MODEL_MODEL)
public class DraftAction {

    @Autowired
    private DraftService draftService;

    /**
     * 创建或修改草稿
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.UPDATE)
    public Draft createOrUpdateDraft(Draft draft) {
        return draftService.createOrUpdateDraft(draft);
    }

    /**
     * 查询用户在该视图下的草稿
     *
     * @param viewIdentifier 页面唯一标识
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public Draft queryDraft(String viewIdentifier) {
        return draftService.queryDraft(viewIdentifier);
    }

    /**
     * 删除页面草稿
     *
     * @param viewIdentifier 页面唯一标识
     */
    @Function(openLevel = {FunctionOpenEnum.API})
    @Function.Advanced(type = FunctionTypeEnum.UPDATE)
    public Draft deleteDraft(String viewIdentifier) {
        // 删除草稿，在前端的create或update执行完应该也调用该方法
        return draftService.deleteDraft(viewIdentifier);
    }

}
