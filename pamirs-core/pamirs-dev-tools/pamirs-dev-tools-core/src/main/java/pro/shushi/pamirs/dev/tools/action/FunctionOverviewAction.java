package pro.shushi.pamirs.dev.tools.action;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.dev.tools.manager.CompareManager;
import pro.shushi.pamirs.dev.tools.model.FunctionOverview;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;


@Model.model(FunctionOverview.MODEL_MODEL)
public class FunctionOverviewAction {

    @Action(displayName = "根据namespace查询fun", bindingType = ViewTypeEnum.FORM)
    public FunctionOverview funByNamespace(FunctionOverview data) {

        String namespace = data.getNamespace();
        String fun = data.getFun();
        if (StringUtils.isBlank(fun)) {
            return null;
        }
        FunctionDefinition cache = PamirsSession.getContext().getFunctionAllowNull(namespace, fun).getFunctionDefinition();
        FunctionDefinition db = new FunctionDefinition().setNamespace(namespace).setFun(fun).queryOne();

        data.setDbContext(JsonUtils.toJSONString(db));
        data.setRedisContext(JsonUtils.toJSONString(cache));
        data.setDiff(CompareManager.compareFun(db, cache));
        return data;
    }

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public FunctionOverview construct(FunctionOverview data) {
        return this.funByNamespace(data);
    }

}
