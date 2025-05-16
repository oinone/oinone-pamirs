package pro.shushi.pamirs.dev.tools.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.cache.api.ActionCacheApi;
import pro.shushi.pamirs.dev.tools.manager.CompareManager;
import pro.shushi.pamirs.dev.tools.model.*;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;


@Model.model(ExtPointOverview.MODEL_MODEL)
public class ExtPointOverviewAction {

    @Action(displayName = "根据namespace查询fun", bindingType = ViewTypeEnum.FORM)
    public ExtPointOverview extPointByNamespace(ExtPointOverview data) {

        String namespace = data.getNamespace();
        String name = data.getName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        List<ExtPointImplementation> caches = new ArrayList<>();
        List<ExtPointImplementation> dbs = new ExtPointImplementation().queryList(
                Pops.<ExtPointImplementation>lambdaQuery().from(ExtPointImplementation.MODEL_MODEL).and(_wrapper->{
                    _wrapper.eq(ExtPointImplementation::getNamespace, namespace);
                    _wrapper.or(_wrapper1->_wrapper1.eq(ExtPointImplementation::getExecuteNamespace,namespace));
                })
        );
        List<ExtPointOverview> extPointOverviews = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dbs)){
            for (ExtPointImplementation db : dbs) {
                ExtPointOverview overview = new ExtPointOverview();
                overview.setDbContext(JsonUtils.toJSONString(db));
                overview.setNamespace(db.getNamespace());
                overview.setName(db.getName());
                overview.setExpression(db.getExpression());
                overview.setExecuteNamespace(db.getExecuteNamespace());
                overview.setExecuteFun(db.getExecuteFun());
                caches = PamirsSession.getContext().getExtPointImplementationList(db.getNamespace(), name);

                if(CollectionUtils.isNotEmpty(caches) ){
                    ExtPointImplementation cache = caches.stream().filter(t->
                            t.getNamespace().equals(db.getNamespace())
                                    && t.getExecuteFun().equals(db.getExecuteFun())
                                    && t.getExpression().equals(db.getExpression())).findFirst().orElse(null);
                    if(cache != null){
                        overview.setRedisContext(JsonUtils.toJSONString(cache));
                        overview.setDiff(CompareManager.compareExtPoint(db, cache));
                    }
                    extPointOverviews.add(overview);
                }
            }
        }
        data.setExtPointOverviews(extPointOverviews);
        return data;
    }

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ExtPointOverview construct(ExtPointOverview data) {
        return this.extPointByNamespace(data);
    }

}
