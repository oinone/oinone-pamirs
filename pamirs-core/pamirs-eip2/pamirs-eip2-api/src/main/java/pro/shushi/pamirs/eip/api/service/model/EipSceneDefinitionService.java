package pro.shushi.pamirs.eip.api.service.model;


import pro.shushi.pamirs.eip.api.model.scene.EipSceneDefinition;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneDefinitionProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;

@Fun(EipSceneDefinitionService.FUN_NAMESPACE)
public interface EipSceneDefinitionService {
    String FUN_NAMESPACE = "pamirs.eip.EipSceneDefinitionService";

    static EipSceneDefinition init(EipSceneDefinition data) {
        return CommonApiFactory.getApi(EipSceneDefinitionService.class).create(data);
    }

    @Function
    EipSceneDefinition create(EipSceneDefinition data);

    @Function
    Integer update(EipSceneDefinition data);

    @Function
    Boolean enable(EipSceneDefinition data);

    @Function
    Boolean disable(EipSceneDefinition data);

    @Function
    EipSceneInstance generateInstance(EipSceneDefinitionProxy data);
}

