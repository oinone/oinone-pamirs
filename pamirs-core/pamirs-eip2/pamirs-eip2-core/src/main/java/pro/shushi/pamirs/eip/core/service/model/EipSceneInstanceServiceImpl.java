package pro.shushi.pamirs.eip.core.service.model;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.behavior.impl.DataStatusBehavior;
import pro.shushi.pamirs.core.common.enmu.CommonExpEnumerate;
import pro.shushi.pamirs.eip.api.constant.EipSceneConstant;
import pro.shushi.pamirs.eip.api.enmu.ComponentTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.EipSceneNodeTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.ParamProcessorTypeEnum;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.executor.EipSceneInstanceExecutor;
import pro.shushi.pamirs.eip.api.model.*;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneDefinition;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneNodeDefinition;
import pro.shushi.pamirs.eip.api.pmodel.EipSceneInstanceProxy;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.eip.api.service.EipSceneInstanceCycleScheduleTaskService;
import pro.shushi.pamirs.eip.api.service.model.EipIncUpdateCursorService;
import pro.shushi.pamirs.eip.api.service.model.EipSceneInstanceService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Fun(EipSceneInstanceService.FUN_NAMESPACE)
public class EipSceneInstanceServiceImpl extends DataStatusBehavior<EipSceneInstance> implements EipSceneInstanceService {

    @Autowired
    private EipInterfaceService eipInterfaceService;

    @Autowired
    private EipSceneInstanceCycleScheduleTaskService eipSceneInstanceCycleScheduleTaskService;

    @Autowired
    private EipIncUpdateCursorService eipIncUpdateLogService;

    @Override
    @Function
    @Transactional
    public EipSceneInstance create(EipSceneInstance data) {
        _baseCheck(data);

        data.construct();
        data = data.create();

        _instanceEnable(data);
        return data;
    }

    private void _baseCheck(EipSceneInstance data) {
        if (data == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_IS_EMPTY).errThrow();
        }
        if (data.getName() == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_NAME_ISNULL).errThrow();
        }
        if (data.getSceneDefinition() == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_SOURCE_SCENE_ISNULL).errThrow();
        }
        if (data.getDataSourceNodeDefinition() == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_DATA_SOURCE_SYSTEM_ISNULL).errThrow();
        }
        if (data.getDataTargetNodeDefinition() == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_DATA_RECEIVE_SYSTEM_ISNULL).errThrow();
        }
    }


    @Override
    @Function
    @Transactional
    public Boolean updateTask(EipSceneInstanceProxy data) {
        EipSceneInstance update = new EipSceneInstance();
        update.setId(data.getId());

        update.setInitTask(data.getInitTask());
        update.setPeriodTimeUnit(data.getPeriodTimeUnit());
        update.setPeriodTimeValue(data.getPeriodTimeValue());
        update.setNextRetryTimeUnit(data.getNextRetryTimeUnit());
        update.setNextRetryTimeValue(data.getNextRetryTimeValue());
        update.setLimitRetryNumber(data.getLimitRetryNumber());
        update.setInstanceBody(data.getInstanceBody());

        update.updateById();

        data = data.queryById();
        if (Boolean.TRUE.equals(data.getInitTask())) {
            //内部处理了更新,不会重复创建
            eipSceneInstanceCycleScheduleTaskService.initTask(data);
        } else {
            eipSceneInstanceCycleScheduleTaskService.cancelTask(data);
        }
        return Boolean.TRUE;
    }

    @Override
    @Function
    @Transactional
    public Boolean updateIncUpdateLog(EipSceneInstanceProxy data) {
        //更新实例配置
        EipSceneInstance update = new EipSceneInstance();
        update.setId(data.getId());

        update.setUseIncUpdateLog(data.getUseIncUpdateLog());
        update.setDefaultStartTime(data.getDefaultStartTime());

        update.updateById();

        // TODO: 2021/8/5 计划支持手动调整增量数据
        return Boolean.TRUE;
    }

    @Function
    @Override
    public Boolean testCall(EipSceneInstanceProxy data) {
        EipSceneInstance exits = data.queryById();
        EipResult result = EipSceneInstanceExecutor
                .newInstance(null)
                .call(exits.getCode(), data.getInstanceBody());
        log.debug("eip场景实例测试,实例id:{},编码:{},入参:{},结果:{}", exits.getId(), exits.getCode(), data.getInstanceBody(), JSON.toJSONString(result));
        return result.getSuccess();
    }

    @Override
    protected EipSceneInstance fetchData(EipSceneInstance data) {
        data = FetchUtil.fetchOne(data);
        if (data == null) {
            throw PamirsException.construct(CommonExpEnumerate.SELECT_NULL).errThrow();
        }
        return data;
    }

    @Function
    @Override
    @Transactional
    public Boolean enable(EipSceneInstance data) {
        data = super.dataStatusEnable(data);
        data.updateById();

        _instanceEnable(data);
        return Boolean.TRUE;
    }

    @Function
    @Override
    @Transactional
    public Boolean disable(EipSceneInstance data) {
        data = super.dataStatusDisable(data);
        data.updateById();

        _instanceDisable(data);
        return Boolean.TRUE;
    }

    /**
     * todo 考虑下缓存?
     *
     * @param code
     * @return
     */
    @Function
    @Override
    public EipSceneInstance queryByCode(String code) {
        return new EipSceneInstance().queryByCode(code);
    }

    /**
     * 节点启用后置处理
     * 初始化EipRouteDefinition
     * 初始化Schedule任务
     *
     * @param data
     */
    private void _instanceEnable(EipSceneInstance data) {
        data = data.queryByPk();
        data.fieldQuery(EipSceneInstance::getSceneDefinition);
        data.fieldQuery(EipSceneInstance::getDataSourceNodeDefinition);
        data.fieldQuery(EipSceneInstance::getDataTargetNodeDefinition);

        _registEipRouteDefinition(data);

        // 初始化schedule任务
        if (Boolean.TRUE.equals(data.getInitTask())) {
            eipSceneInstanceCycleScheduleTaskService.initTask(data);
        }
    }

    /**
     * 节点禁用后置处理
     * 删除schedule任务
     *
     * @param data
     */
    private void _instanceDisable(EipSceneInstance data) {
        data = data.queryByPk();
        if (Boolean.TRUE.equals(data.getInitTask())) {
            eipSceneInstanceCycleScheduleTaskService.cancelTask(data);
        }
    }

    /**
     * 根据场景实例生成路由
     *
     * @param data
     */
    private void _registEipRouteDefinition(EipSceneInstance data) {
        EipSceneDefinition sceneDefinition = data.getSceneDefinition();
        EipRouteDefinition eipRouteDefinition = data.getEipRouteDefinition();
        if (eipRouteDefinition == null) {
            eipRouteDefinition = new EipRouteDefinition();
        }
        eipRouteDefinition.setName(EipSceneConstant.ROUTE_NAME_PREFIX + EipSceneConstant.ROUTE_NAME_SEPARATOR + sceneDefinition.getName() + EipSceneConstant.ROUTE_NAME_SEPARATOR + data.getName());
        // 默认前缀 + 场景技术名称 + 来源节点编码 + 接收节点编码
        // 保证一个场景下,相同组合的实例的eipRouteDefinition是复用的. 现在一个实例对应1个schedule任务(或许列表会更好点?感觉坑了)
        eipRouteDefinition.setInterfaceName(EipSceneConstant.ROUTE_INTERFACE_NAME_PREFIX + EipSceneConstant.ROUTE_NAME_SEPARATOR + sceneDefinition.getSceneName() + EipSceneConstant.ROUTE_NAME_SEPARATOR + data.getDataSourceNodeDefinition().getCode() + EipSceneConstant.ROUTE_NAME_SEPARATOR + data.getDataTargetNodeDefinition().getCode());

        List<EipComponentDefinition> definitions = new ArrayList<>();
        _buildEipComponent(definitions, data.getDataSourceNodeDefinition());
        _buildEipComponent(definitions, data.getDataTargetNodeDefinition());
        eipRouteDefinition.setDefinitions(definitions);

        //路由定义入库
        eipRouteDefinition.construct();
        eipRouteDefinition.createOrUpdate();
        data.setEipRouteDefinition(eipRouteDefinition);
        data.fieldSave(EipSceneInstance::getEipRouteDefinition);

        //注册到camel
        eipInterfaceService.registerRouteDefinition(eipRouteDefinition);
    }

    /**
     * 构造路由组件
     *
     * @param definitions
     * @param sceneNodeDefinition
     */
    private void _buildEipComponent(List<EipComponentDefinition> definitions, EipSceneNodeDefinition sceneNodeDefinition) {
        _buildEipParamProcessorComponent(definitions, sceneNodeDefinition,
                EipSceneNodeDefinition::getInConverterFunction,
                EipSceneNodeDefinition::getInConvertParamList,
                EipSceneNodeDefinition::getInFinalResultKey);

        _buildEipCoreComponent(definitions, sceneNodeDefinition);

        _buildEipParamProcessorComponent(definitions, sceneNodeDefinition,
                EipSceneNodeDefinition::getOutConverterFunction,
                EipSceneNodeDefinition::getOutConvertParamList,
                EipSceneNodeDefinition::getOutFinalResultKey);
    }

    private void _buildEipCoreComponent(List<EipComponentDefinition> definitions, EipSceneNodeDefinition sceneNodeDefinition) {
        EipSceneNodeTypeEnum nodeTypeEnum = sceneNodeDefinition.getNodeTypeEnum();
        FunctionDefinition function = sceneNodeDefinition.getFunction();
        EipIntegrationInterface eipInterface = sceneNodeDefinition.getEipIntegrationInterface();
        EipRouteDefinition eipRouteDefinition = sceneNodeDefinition.getEipRouteDefinition();

        EipComponentDefinition componentDefinition = new EipComponentDefinition();
        switch (nodeTypeEnum) {
            case FUNCTION:
                componentDefinition.setType(ComponentTypeEnum.FUNCTION);
                componentDefinition.setConvertNamespace(function.getNamespace());
                componentDefinition.setConvertFun(function.getFun());
                definitions.add(componentDefinition);
                return;
            case INTEGRATION_INTERFACE:
                componentDefinition.setType(ComponentTypeEnum.NORMAL);
                componentDefinition.setInterfaceName(eipInterface.getInterfaceName());
                definitions.add(componentDefinition);
                return;
            case ROUTE_DEFINITION:
                //选择了组合接口,将组合接口中的所有节点,加入到新的组合接口中
                eipRouteDefinition = eipRouteDefinition.queryOne();
                definitions.addAll(eipRouteDefinition.getDefinitions());
                return;
            default:
                //创建时判断了,理论上不可能
                throw PamirsException.construct(EipExpEnumerate.SCENE_DEFINITION_NODE_UNKNOWN_TYPE).errThrow();
        }

    }

    private void _buildEipParamProcessorComponent(List<EipComponentDefinition> definitions, EipSceneNodeDefinition sceneNodeDefinition,
                                                  java.util.function.Function<EipSceneNodeDefinition, FunctionDefinition> converterFunctionFunc,
                                                  java.util.function.Function<EipSceneNodeDefinition, List<EipConvertParam>> convertParamListFunc,
                                                  java.util.function.Function<EipSceneNodeDefinition, String> finalResultKeyFunc) {
        FunctionDefinition converterFunction = converterFunctionFunc.apply(sceneNodeDefinition);
        List<EipConvertParam> convertParamList = convertParamListFunc.apply(sceneNodeDefinition);
        String finalResultKey = finalResultKeyFunc.apply(sceneNodeDefinition);
        EipComponentDefinition componentDefinition = new EipComponentDefinition();
        componentDefinition.setType(ComponentTypeEnum.PARAM_PROCESSOR);
        if (converterFunction == null && CollectionUtils.isEmpty(convertParamList) && StringUtils.isEmpty(finalResultKey)) {
//                        paramProcessor = new EipParamProcessor(); 不默认执行转换器
            return;
        }
        EipParamProcessor paramProcessor = new EipParamProcessor();
        paramProcessor.setConvertParamList(convertParamList);
        paramProcessor.setFinalResultKey(finalResultKey);
        if (converterFunction != null) {
            paramProcessor.setInOutConverterNamespace(converterFunction.getNamespace());
            paramProcessor.setInOutConverterFun(converterFunction.getFun());
            paramProcessor.setInOutConverterFunction(converterFunction);
        }
        // FIXME: 2021/10/28
        paramProcessor.setType(ParamProcessorTypeEnum.REQUEST);

        //core不依赖view
//        EipIntegrationInterfaceEdit interfaceEdit = new EipIntegrationInterfaceEdit();
//        interfaceEdit.setReqConvertParamList(convertParamList)
//                .setReqFinalResultKey(finalResultKey)
//                .setReqInOutConverterFunction(converterFunction)
////                    .setConverterFunction(converterFunction)
//        ;
//        paramProcessor = eipInterfaceEditConvertService.convert(interfaceEdit);

        componentDefinition.setParamProcessor(paramProcessor);
        definitions.add(componentDefinition);
    }
}
