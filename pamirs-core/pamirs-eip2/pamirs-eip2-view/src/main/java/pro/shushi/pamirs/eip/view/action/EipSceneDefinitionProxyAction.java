package pro.shushi.pamirs.eip.view.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneDefinition;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneNodeDefinition;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneDefinitionProxy;
import pro.shushi.pamirs.eip.api.service.model.EipSceneDefinitionService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Model.model(EipSceneDefinitionProxy.MODEL_MODEL)
public class EipSceneDefinitionProxyAction {

    @Autowired
    private EipSceneDefinitionService eipSceneDefinitionService;

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<EipSceneDefinitionProxy> queryPage(Pagination<EipSceneDefinition> page, IWrapper<EipSceneDefinition> queryWrapper) {
        Pagination<EipSceneDefinition> pagination = Models.origin().queryPage(page, queryWrapper);
        Pagination<EipSceneDefinitionProxy> proxyPagination = new Pagination<>();
        if (CollectionUtils.isEmpty(pagination.getContent())) {
            return proxyPagination;
        }
        proxyPagination = pagination.to(proxyPagination);
        proxyPagination.setContent(ArgUtils.convert(EipSceneDefinition.MODEL_MODEL, EipSceneDefinitionProxy.MODEL_MODEL, pagination.getContent()));

        Models.origin().listFieldQuery(proxyPagination.getContent(), EipSceneDefinitionProxy::getDataSourceNodeDefinitions);
        Models.origin().listFieldQuery(proxyPagination.getContent(), EipSceneDefinitionProxy::getDataTargetNodeDefinitions);
        Models.origin().listFieldQuery(proxyPagination.getContent(), EipSceneDefinitionProxy::getSceneInstances);

        //将每个定义,对应的2个系统列表,中的module放在一个集合里
        List<String> modules = Stream.concat(
                        proxyPagination.getContent()
                                .stream()
                                .map(EipSceneDefinition::getDataSourceNodeDefinitions),
                        proxyPagination.getContent()
                                .stream()
                                .map(EipSceneDefinition::getDataTargetNodeDefinitions)
                )
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .map(EipSceneNodeDefinition::getDataModuleModule)
                .collect(Collectors.toList());
        Map<String, ModuleDefinition> moduleMap = _findModules(modules);

        for (EipSceneDefinitionProxy eipSceneDefinitionProxy : proxyPagination.getContent()) {
            eipSceneDefinitionProxy.setDataSourceStr(
                    CollectionUtils.isEmpty(eipSceneDefinitionProxy.getDataSourceNodeDefinitions()) ? "" :
                            eipSceneDefinitionProxy.getDataSourceNodeDefinitions()
                                    .stream()
                                    .map(i -> moduleMap.get(i.getDataModuleModule()))
                                    .filter(Objects::nonNull)
                                    .map(ModuleDefinition::getDisplayName)
                                    .collect(Collectors.joining(","))
            );
            eipSceneDefinitionProxy.setDataTargetStr(
                    CollectionUtils.isEmpty(eipSceneDefinitionProxy.getDataTargetNodeDefinitions()) ? "" :
                            eipSceneDefinitionProxy.getDataTargetNodeDefinitions()
                                    .stream()
                                    .map(i -> moduleMap.get(i.getDataModuleModule()))
                                    .filter(Objects::nonNull)
                                    .map(ModuleDefinition::getDisplayName)
                                    .collect(Collectors.joining(","))
            );
            eipSceneDefinitionProxy.setInstanceNum(
                    CollectionUtils.isEmpty(eipSceneDefinitionProxy.getSceneInstances()) ? 0 : eipSceneDefinitionProxy.getSceneInstances().size()
            );
        }
        return proxyPagination;
    }

    /**
     * PamirsSession.getContext()里没有displayName,所以此处批量查数据库
     *
     * @param modules
     * @return
     */
    private Map<String, ModuleDefinition> _findModules(List<String> modules) {
        if (CollectionUtils.isEmpty(modules)) {
            return new HashMap<>();
        }
        List<ModuleDefinition> list = Models.origin().queryListByWrapper(
                Pops.<ModuleDefinition>lambdaQuery()
                        .from(ModuleDefinition.MODEL_MODEL)
                        .in(ModuleDefinition::getModule, modules)
        );
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(ModuleDefinition::getModule, i -> i, (a, b) -> a));
    }

    @Action.Advanced(name = FunctionConstants.create, managed = true)
    @Action(displayName = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public EipSceneDefinitionProxy create(EipSceneDefinitionProxy data) {
        eipSceneDefinitionService.create(data);
        return data;
    }

    @Action.Advanced(name = FunctionConstants.update, managed = true)
    @Action(displayName = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public EipSceneDefinitionProxy update(EipSceneDefinitionProxy data) {
        eipSceneDefinitionService.update(data);
        return data;
    }

    @Action(displayName = "确定", bindingType = ViewTypeEnum.FORM, summary = "根据场景定义生成实例")
    public EipSceneDefinitionProxy generateInstance(EipSceneDefinitionProxy data) {
        eipSceneDefinitionService.generateInstance(data);
        return data;
    }

    @Action.Advanced(invisible = "context.activeRecord.dataStatus != 'NOT_ENABLED' && context.activeRecord.dataStatus != 'DISABLED'")
    @Action(displayName = "启用", contextType = ActionContextTypeEnum.SINGLE)
    public EipSceneDefinitionProxy dataStatusEnable(EipSceneDefinitionProxy data) {
        eipSceneDefinitionService.enable(data);
        return data;
    }

    @Action.Advanced(invisible = "context.activeRecord.dataStatus != 'ENABLED'")
    @Action(displayName = "禁用", contextType = ActionContextTypeEnum.SINGLE)
    public EipSceneDefinitionProxy dataStatusDisable(EipSceneDefinitionProxy data) {
        eipSceneDefinitionService.disable(data);
        return data;
    }
}
