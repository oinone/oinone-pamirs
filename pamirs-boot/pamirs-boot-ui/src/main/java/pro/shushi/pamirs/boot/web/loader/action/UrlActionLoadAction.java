package pro.shushi.pamirs.boot.web.loader.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.utils.LoaderUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.boot.common.util.MetaOnlineLocalUtil.loadLocal;


@Base
@Service
@Model.model(UrlAction.MODEL_MODEL)
public class UrlActionLoadAction {

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "加载URL动作")
    public UrlAction load(UrlAction request) {
        Long id = request.getId();
        String model = request.getModel();
        String name = request.getName();

        boolean loadLocal = loadLocal();
        UrlAction result = request;
        if (loadLocal) {
            Action cacheAction = PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(model, name);
            if (cacheAction instanceof UrlAction) {
                result = (UrlAction) BeanDefinitionUtils.getBean(UiIoManager.class).cloneData(cacheAction);
            }
        } else {
            LambdaQueryWrapper<UrlAction> wrapper = Pops.<UrlAction>lambdaQuery().from(UrlAction.MODEL_MODEL);
            if (StringUtils.isNotBlank(name)) {
                result = Models.data().queryOneByWrapper(LoaderUtils.authQuery(wrapper).eq(UrlAction::getModel, model).eq(UrlAction::getName, name));
            } else if (null != id) {
                result = Models.data().queryOneByWrapper(LoaderUtils.authQuery(wrapper).eq(UrlAction::getId, id));
            }
        }
        return result;
    }

}
