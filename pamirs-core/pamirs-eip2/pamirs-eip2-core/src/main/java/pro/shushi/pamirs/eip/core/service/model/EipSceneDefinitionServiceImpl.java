package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.behavior.impl.DataStatusBehavior;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneDefinition;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneNodeDefinition;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneDefinitionProxy;
import pro.shushi.pamirs.eip.api.service.model.EipSceneDefinitionService;
import pro.shushi.pamirs.eip.api.service.model.EipSceneInstanceService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Fun(EipSceneDefinitionService.FUN_NAMESPACE)
public class EipSceneDefinitionServiceImpl extends DataStatusBehavior<EipSceneDefinition> implements EipSceneDefinitionService {

    @Autowired
    private EipSceneInstanceService eipSceneInstanceService;

    @Override
    @Function
    @Transactional
    public EipSceneDefinition create(EipSceneDefinition data) {
        _baseCheck(data);

        data.construct();
        data = data.create();

        data.fieldSave(EipSceneDefinition::getDataSourceNodeDefinitions);
        data.fieldSave(EipSceneDefinition::getDataTargetNodeDefinitions);
        return data;
    }

    /**
     * fixme 编辑时,如果改了已经生成实例的节点,会导致定义是eipRouteDefinition不一致
     *
     * @param data
     * @return
     */
    @Override
    @Function
    @Transactional
    public Integer update(EipSceneDefinition data) {
        _baseCheck(data);

        data.updateById();

        EipSceneDefinition source = data.queryById();
        source.fieldQuery(EipSceneDefinition::getDataSourceNodeDefinitions);
        source.fieldQuery(EipSceneDefinition::getDataTargetNodeDefinitions);

        //差量
        if (CollectionUtils.isNotEmpty(source.getDataSourceNodeDefinitions()) && CollectionUtils.isNotEmpty(data.getDataSourceNodeDefinitions())) {
            List<Long> exitsIds = data.getDataSourceNodeDefinitions().stream().map(IdModel::getId).filter(Objects::nonNull).collect(Collectors.toList());
            source.setDataSourceNodeDefinitions(source.getDataSourceNodeDefinitions().stream().filter(i -> !exitsIds.contains(i.getId())).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(source.getDataTargetNodeDefinitions()) && CollectionUtils.isNotEmpty(data.getDataTargetNodeDefinitions())) {
            List<Long> exitsIds = data.getDataTargetNodeDefinitions().stream().map(IdModel::getId).filter(Objects::nonNull).collect(Collectors.toList());
            source.setDataTargetNodeDefinitions(source.getDataTargetNodeDefinitions().stream().filter(i -> !exitsIds.contains(i.getId())).collect(Collectors.toList()));
        }

        source.relationDelete(EipSceneDefinition::getDataSourceNodeDefinitions);
        source.relationDelete(EipSceneDefinition::getDataTargetNodeDefinitions);

        data.fieldSave(EipSceneDefinition::getDataSourceNodeDefinitions);
        data.fieldSave(EipSceneDefinition::getDataTargetNodeDefinitions);
        return null;
    }

    /**
     * 数据校验. 更新场景只考虑前端触发(该有的字段都有.而不是不传不更新)
     *
     * @param sceneDefinition
     */
    private void _baseCheck(EipSceneDefinition sceneDefinition) {
        if (sceneDefinition == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_IS_EMPTY).errThrow();
        }
        if (StringUtils.isEmpty(sceneDefinition.getName())) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NAME_ISNULL).errThrow();
        }
        if (StringUtils.isEmpty(sceneDefinition.getSceneName())) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_TECH_NAME_ISNULL).errThrow();
        }
        if (CollectionUtils.isEmpty(sceneDefinition.getDataSourceNodeDefinitions())) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_LIST_IS_EMPTY).errThrow();
        }
        if (CollectionUtils.isEmpty(sceneDefinition.getDataTargetNodeDefinitions())) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_LIST_IS_EMPTY).errThrow();
        }
        sceneDefinition.getDataSourceNodeDefinitions().forEach(this::_sceneNodeCheck);
        sceneDefinition.getDataTargetNodeDefinitions().forEach(this::_sceneNodeCheck);
    }

    private void _sceneNodeCheck(EipSceneNodeDefinition sceneNodeDefinition) {
        if (sceneNodeDefinition == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_EMPTY).errThrow();
        }
        if (sceneNodeDefinition.getDataModule() == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_DATA_SYSTEM_ISNULL).errThrow();
        }
        if (sceneNodeDefinition.getNodeTypeEnum() == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_TYPE_ISNULL).errThrow();
        }
        switch (sceneNodeDefinition.getNodeTypeEnum()) {
            case FUNCTION:
                if (sceneNodeDefinition.getFunction() == null) {
                    throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_UNSET_FUN).errThrow();
                }
                break;
            case INTEGRATION_INTERFACE:
                if (sceneNodeDefinition.getEipIntegrationInterface() == null) {
                    throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_UNSET_API).errThrow();
                }
                break;
            case ROUTE_DEFINITION:
                if (sceneNodeDefinition.getEipRouteDefinition() == null) {
                    throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_UNSET_COMPOSITE_API).errThrow();
                }
                break;
            default:
                throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_UNKNOWN_TYPE).errThrow();
        }
    }

    @Override
    protected EipSceneDefinition fetchData(EipSceneDefinition data) {
        data = FetchUtil.fetchOne(data);
        if (data == null) {
            throw PamirsException.construct(CommonExpEnumerate.SELECT_NULL).errThrow();
        }
        return data;
    }

    @Function
    @Override
    @Transactional
    public Boolean enable(EipSceneDefinition data) {
        data = super.dataStatusEnable(data);
        data.updateById();
        // 不自动启用实例
        return Boolean.TRUE;
    }

    @Function
    @Override
    @Transactional
    public Boolean disable(EipSceneDefinition data) {
        data = super.dataStatusDisable(data);
        data.updateById();

        // 禁用所有场景实例
        data = data.queryById();
        data.fieldQuery(EipSceneDefinition::getSceneInstances);
        if (CollectionUtils.isNotEmpty(data.getSceneInstances())) {
            data.getSceneInstances().forEach(eipSceneInstanceService::disable);
        }
        return Boolean.TRUE;
    }

    @Function
    @Override
    public EipSceneInstance generateInstance(EipSceneDefinitionProxy data) {
        EipSceneInstance sceneInstance = new EipSceneInstance();
        sceneInstance.setSceneDefinition(data);

        data.fieldQuery(EipSceneDefinitionProxy::getDataSourceNodeDefinitions);
        data.fieldQuery(EipSceneDefinitionProxy::getDataTargetNodeDefinitions);
        sceneInstance.setDataSourceNodeDefinition(_matchSceneNode(data.getDataSourceNodeDefinitions(), data.getDataSourceNode()));
        sceneInstance.setDataTargetNodeDefinition(_matchSceneNode(data.getDataTargetNodeDefinitions(), data.getDataTargetNode()));
        // TODO: 2021/8/9 实例名称未支持前端指定,这里按规则构造下
        sceneInstance.setName("from:" + sceneInstance.getDataSourceNodeDefinition().getName() + ";to:" + sceneInstance.getDataTargetNodeDefinition().getName());

        //定时任务
        sceneInstance.setInitTask(data.getInitTask());
        sceneInstance.setPeriodTimeUnit(data.getPeriodTimeUnit());
        sceneInstance.setPeriodTimeValue(data.getPeriodTimeValue());
        sceneInstance.setNextRetryTimeUnit(data.getNextRetryTimeUnit());
        sceneInstance.setNextRetryTimeValue(data.getNextRetryTimeValue());
        sceneInstance.setLimitRetryNumber(data.getLimitRetryNumber());
        sceneInstance.setInstanceBody(data.getInstanceBody());

        //增量日志
        sceneInstance.setUseIncUpdateLog(data.getUseIncUpdateLog());
        sceneInstance.setDefaultStartTime(data.getDefaultStartTime());

        return eipSceneInstanceService.create(sceneInstance);
    }

    /**
     * 从支持的系统节点中,匹配选中的
     *
     * @param nodeDefinitions
     * @param selectNode
     * @return
     */
    private EipSceneNodeDefinition _matchSceneNode(List<EipSceneNodeDefinition> nodeDefinitions, EipSceneNodeDefinition selectNode) {
        if (selectNode == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_CREATE_UNSELECT_DATA_NODE).errThrow();
        }
        EipSceneNodeDefinition nodeDefinition = nodeDefinitions.stream().filter(i -> i.getId().equals(selectNode.getId())).findFirst().orElse(null);
        if (nodeDefinition != null) {
            return nodeDefinition;
        }
        throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_CREATE_SELECT_ERROR_DATA_NODE).errThrow();
    }
}
