package pro.shushi.pamirs.boot.web.spi.meta;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedExtendProcessor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.List;
import java.util.Optional;

/**
 * base 继承逻辑扩展
 * <p>
 * 2020/4/26 11:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order(1)
@Component
public class ServerActionInheritedExtendProcessor implements InheritedExtendProcessor {

    @Override
    public void abstractInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

    @Override
    public void transientInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

    @Override
    public void multiTableInherit(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

    @Override
    public void extend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        List<FunctionDefinition> superFunctionDefinitionList = superModel.getFunctions();
        if (CollectionUtils.isEmpty(superFunctionDefinitionList)) {
            return;
        }
        for (FunctionDefinition superFunction : superFunctionDefinitionList) {
            if (superFunction.isMetaCompleted()) {
                continue;
            }
            String namespace = superFunction.getNamespace();
            String name = superFunction.getName();
            String superSign = ServerAction.sign(namespace, name);
            Pair<String, ServerAction> superServerActionWithModule
                    = meta.getDataItemWithModule(ServerAction.MODEL_MODEL, superSign);
            ServerAction superServerAction = superServerActionWithModule.getValue();
            if (null != superServerAction) {
                if (superServerAction.isMetaCompleted()) {
                    continue;
                }
                MetaData metaData = meta.getData().get(modelDefinition.getModule());
                String sign = ServerAction.sign(modelDefinition.getModel(), name);
                ServerAction selfServerAction = Optional.ofNullable(meta.getData().get(modelDefinition.getModule()))
                        .map(v -> (ServerAction) v.getDataItem(ServerAction.MODEL_MODEL, sign)).orElse(null);
                if (null != selfServerAction) {
                    if (!selfServerAction.isMetaCompleted()) {
                        continue;
                    }
                    String crossingModule = metaData.getCrossingModule(ServerAction.MODEL_MODEL, selfServerAction.getSign());
                    if (null != crossingModule && !meta.isInBootModules(crossingModule)) {
                        selfServerAction.disableMetaCompleted();
                        continue;
                    }
                }

                ServerAction newServerAction = ObjectUtils.clone(superServerAction);
                newServerAction.disableMetaCompleted();
                if (null != selfServerAction) {
                    newServerAction.setId(selfServerAction.getId());
                    newServerAction.setHash(selfServerAction.getHash());
                    newServerAction.setStringify(selfServerAction.getStringify());
                } else {
                    newServerAction.setId(null);
                    newServerAction.setHash(null);
                    newServerAction.setStringify(null);
                }
                newServerAction.setModel(modelDefinition.getModel());
                newServerAction.setModelDefinition(modelDefinition);
                newServerAction.setSign(null);
                newServerAction.setSign(newServerAction.getSign());
                newServerAction.setSystemSource(SystemSourceEnum.EXTEND_INHERITED);
                newServerAction.setBitOptions(superServerAction.getBitOptions());
                newServerAction.construct(selfServerAction);
                if (null != newServerAction.getFunctionDefinition()) {
                    newServerAction.getFunctionDefinition().setNamespace(newServerAction.getModel());
                }
                metaData.addData(newServerAction);
                String superModule = superServerActionWithModule.getKey();
                MetaData superMetaData = meta.getData().get(superModule);
                String sourceModule = superMetaData.getCrossingModule(ServerAction.MODEL_MODEL, superSign);
                if (null != sourceModule) {
                    metaData.addCrossingExtendData(ServerAction.MODEL_MODEL, sign, sourceModule);
                    meta.placeCrossingMetadata(sourceModule, newServerAction);
                }
            }
        }
    }

    @Override
    public void proxyExtend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

}
