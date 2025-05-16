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
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;


//import static pro.shushi.pamirs.distribution.session.redis.SessionRedisUtils.RedisTemplateHolders.byteOps;

@Model.model(ModelOverview.MODEL_MODEL)
public class ModelOverviewAction {

    @Action(displayName = "根据model查询", bindingType = ViewTypeEnum.FORM)
    public ModelOverview modelByModel(ModelOverview data) {
//        RedisStandaloneConfiguration standaloneConfiguration = ((JedisConnectionFactory) byteOps().getConnectionFactory()).getStandaloneConfiguration();
//        data.setRedisInfo(new RedisInfo()
//                .setHost(standaloneConfiguration.getHostName())
//                .setDatabase(standaloneConfiguration.getDatabase())
//                .setPort(standaloneConfiguration.getPort())
//        );
//        PamirsSession.getContext().getExtPointImplementationList()
        String model = data.getModel();
        if(StringUtils.isBlank(model)){
            return null;
        }
        List<ModelFieldOverview> fieldOverviews = new ArrayList<>();
        UeModel modelDefinition = new UeModel().setModel(model).queryOne();
        data.setDbContext(JsonUtils.toJSONString(modelDefinition));
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);

        data.setRedisContext(JsonUtils.toJSONString(modelConfig));
        data.setDiff(CompareManager.compareModel(modelDefinition,modelConfig));

        modelDefinition.fieldQuery(ModelDefinition::getModelFields);
        List<ModelField> modelFields = modelDefinition.getModelFields();
        if(CollectionUtils.isNotEmpty(modelFields)){
            for (ModelField modelField : modelFields) {
                ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model,modelField.getField());

                ModelFieldOverview fieldOverview = new ModelFieldOverview();
                fieldOverview.setModel(model)
                        .setField(modelField.getField())
                        .setDbContext(JsonUtils.toJSONString(modelField))
                        .setRedisContext(JsonUtils.toJSONString(modelFieldConfig))
                        .setDiff(CompareManager.compareField(modelField,modelFieldConfig));
                fieldOverviews.add(fieldOverview);
            }
        }

        if(CollectionUtils.isNotEmpty(modelConfig.getModelFieldConfigList())){
            for (ModelFieldConfig fieldConfig : modelConfig.getModelFieldConfigList()) {
                if(fieldOverviews.stream().noneMatch(t->t.getField().equals(fieldConfig.getField()))){
                    ModelFieldOverview fieldOverview = new ModelFieldOverview();
                    fieldOverview.setModel(model)
                            .setField(fieldConfig.getField())
                            .setRedisContext(JsonUtils.toJSONString(fieldConfig))
                            .setDiff(CompareManager.compareField(null,fieldConfig));

                    fieldOverviews.add(fieldOverview);
                }
            }
        }
        data.setModelFieldOverviews(fieldOverviews);


        List<ServerAction> serverActions = new ServerAction().setModel(model).queryList();
        List<ViewAction> viewActions = new ViewAction().setModel(model).queryList();
        List<UrlAction> urlActions = new UrlAction().setModel(model).queryList();

        if(CollectionUtils.isNotEmpty(serverActions)){
            List<ModelServerActionOverview> overviews = new ArrayList<>();
            for (ServerAction serverAction : serverActions) {
                ModelServerActionOverview overview = new ModelServerActionOverview();
                overview.setModel(model)
                        .setFun(serverAction.getFun())
                        .setDbContext(JsonUtils.toJSONString(serverAction));
                ServerAction cacheAction = (ServerAction) PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(model, serverAction.getFun());
                if(cacheAction != null){
                    overview.setRedisContext(JsonUtils.toJSONString(cacheAction));
                }
                overview.setDiff(CompareManager.compareServerAction(serverAction,cacheAction));

                overviews.add(overview);
            }
            data.setServerActionOverviews(overviews);
        }

        if(CollectionUtils.isNotEmpty(viewActions)){
            List<ModelViewActionOverview> overviews = new ArrayList<>();
            for (ViewAction viewAction : viewActions) {
                ModelViewActionOverview overview = new ModelViewActionOverview();
                overview.setModel(model)
                        .setName(viewAction.getName())
                        .setDbContext(JsonUtils.toJSONString(viewAction));
                ViewAction cacheAction = (ViewAction) PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(model, viewAction.getName());
                if(cacheAction != null){
                    overview.setRedisContext(JsonUtils.toJSONString(cacheAction));
                }
                overview.setDiff(CompareManager.compareViewAction(viewAction,cacheAction));

                overviews.add(overview);
            }
            data.setViewActionOverviews(overviews);
        }

        if(CollectionUtils.isNotEmpty(urlActions)){
            List<ModelUrlActionOverview> overviews = new ArrayList<>();
            for (UrlAction urlAction : urlActions) {
                ModelUrlActionOverview overview = new ModelUrlActionOverview();
                overview.setModel(model)
                        .setUrl(urlAction.getUrl())
                        .setDbContext(JsonUtils.toJSONString(urlAction));
                UrlAction cacheAction = (UrlAction) PamirsSession.getContext().getExtendCache(ActionCacheApi.class).get(model, urlAction.getName());
                if(cacheAction != null){
                    overview.setRedisContext(JsonUtils.toJSONString(cacheAction));
                }
                overview.setDiff(CompareManager.compareUrlAction(urlAction,cacheAction));

                overviews.add(overview);
            }
            data.setUrlActionOverviews(overviews);

        }

        return data;
    }

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ModelOverview construct(ModelOverview data) {

        return this.modelByModel(data);
    }

}
