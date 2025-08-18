package pro.shushi.pamirs.boot.web.spi.meta;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.api.core.compute.systems.inherit.InheritedExtendProcessor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.*;

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
            List<Pair<String, ServerAction>> superServerActions = getSuperServerActions(meta, superFunction.getNamespace(), superFunction.getFun(), superFunction.getName());
            for (Pair<String, ServerAction> superServerActionWithModule : superServerActions) {
                ServerAction superServerAction = superServerActionWithModule.getValue();
                if (superServerAction.isMetaCompleted()) {
                    continue;
                }
                String namespace = superServerAction.getModel();
                String name = superServerAction.getName();
                String superSign = ServerAction.sign(namespace, name);
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

    @SuppressWarnings("unchecked")
    private List<Pair<String, ServerAction>> getSuperServerActions(Meta meta, String namespace, String fun, String name) {
        List<Pair<String, ServerAction>> serverActionWithModules = new ArrayList<>();
        String sign = ServerAction.sign(namespace, name);
        Pair<String, ServerAction> serverActionWithModule = meta.getDataItemWithModule(ServerAction.MODEL_MODEL, sign);
        if (serverActionWithModule.getValue() != null) {
            serverActionWithModules.add(serverActionWithModule);
        }
        List<ServerAction> otherServerActions = new ArrayList<>();
        List<MetaData> metaDataList = new ArrayList<>(meta.getData().values());
        Collections.reverse(metaDataList);
        for (MetaData metaData : metaDataList) {
            Optional.ofNullable(metaData.getDataMap(ServerAction.MODEL_MODEL))
                    .map(v -> new ArrayList<>((Collection<ServerAction>) (Object) v.values()))
                    .ifPresent(otherServerActions::addAll);
        }
        if (CollectionUtils.isEmpty(otherServerActions)) {
            return serverActionWithModules;
        }
        Set<String> signSet = new HashSet<>();
        signSet.add(sign);
        for (ServerAction otherServerAction : otherServerActions) {
            String model = otherServerAction.getModel();
            if (!namespace.equals(model)) {
                continue;
            }
            String action = otherServerAction.getName();
            String actionSign = ServerAction.sign(model, action);
            if (signSet.contains(actionSign)) {
                continue;
            }
            signSet.add(actionSign);
            Pair<String, ServerAction> otherServerActionWithModule = meta.getDataItemWithModule(ServerAction.MODEL_MODEL, actionSign);
            if (otherServerActionWithModule.getValue() != null) {
                serverActionWithModules.add(otherServerActionWithModule);
            }
        }
        return serverActionWithModules;
    }

    @Override
    public void proxyExtend(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel) {
        extend(meta, modelDefinition, superModel);
    }

}
