package pro.shushi.pamirs.dev.tools.action;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.dev.tools.manager.CompareManager;
import pro.shushi.pamirs.dev.tools.model.DictionaryOverview;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

@Model.model(DictionaryOverview.MODEL_MODEL)
public class DictionaryOverviewAction {
    @Action(displayName = "根据Dictionary查询字典", bindingType = ViewTypeEnum.FORM)
    public DictionaryOverview dictionaryByCode(DictionaryOverview data) {

        String dictionary = data.getDictionary();
        if (StringUtils.isBlank(dictionary)) {
            return null;
        }
        DataDictionary cache = PamirsSession.getContext().getDictionary(dictionary);
        DataDictionary db = new DataDictionary().setDictionary(dictionary).queryOne();

        data.setDbContext(JsonUtils.toJSONString(db));
        data.setRedisContext(JsonUtils.toJSONString(cache));
        data.setDiff(CompareManager.compareDictionary(db, cache));
        return data;
    }

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public DictionaryOverview construct(DictionaryOverview data) {
        return this.dictionaryByCode(data);
    }

}
