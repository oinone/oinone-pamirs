package pro.shushi.pamirs.eip.api.service.model;


import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneInstanceProxy;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

@Fun(EipSceneInstanceService.FUN_NAMESPACE)
public interface EipSceneInstanceService {
    String FUN_NAMESPACE = "pamirs.eip.EipSceneInstanceService";

    @Function
    EipSceneInstance create(EipSceneInstance data);

    @Function
    Boolean updateTask(EipSceneInstanceProxy data);

    @Function
    Boolean updateIncUpdateLog(EipSceneInstanceProxy data);

    @Function
    Boolean testCall(EipSceneInstanceProxy data);

    @Function
    Boolean enable(EipSceneInstance data);

    @Function
    Boolean disable(EipSceneInstance data);

    @Function
    EipSceneInstance queryByCode(String code);
}

