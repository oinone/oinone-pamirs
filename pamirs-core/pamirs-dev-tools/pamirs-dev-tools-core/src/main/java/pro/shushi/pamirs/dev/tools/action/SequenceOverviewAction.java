package pro.shushi.pamirs.dev.tools.action;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.dev.tools.manager.CompareManager;
import pro.shushi.pamirs.dev.tools.model.DictionaryOverview;
import pro.shushi.pamirs.dev.tools.model.SequenceOverview;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

@Model.model(SequenceOverview.MODEL_MODEL)
public class SequenceOverviewAction {
    @Action(displayName = "根据序列编码查询", bindingType = ViewTypeEnum.FORM)
    public SequenceOverview sequenceByCode(SequenceOverview data) {

        String code = data.getCode();
        if (StringUtils.isBlank(code)) {
            return null;
        }
        SequenceConfig cache = PamirsSession.getContext().getSequenceConfig(code);
        SequenceConfig db = new SequenceConfig().setCode(code).queryOne();

        data.setDbContext(JsonUtils.toJSONString(db));
        data.setRedisContext(JsonUtils.toJSONString(cache));
        data.setDiff(CompareManager.compareSequence(db, cache));
        return data;
    }

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public SequenceOverview construct(SequenceOverview data) {
        return this.sequenceByCode(data);
    }

}
