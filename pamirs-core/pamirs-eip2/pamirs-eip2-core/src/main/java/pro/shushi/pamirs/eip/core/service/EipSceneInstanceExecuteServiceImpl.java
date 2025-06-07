package pro.shushi.pamirs.eip.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.constant.EipSceneConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.model.EipIncUpdateCursor;
import pro.shushi.pamirs.eip.api.model.scene.EipSceneInstance;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;
import pro.shushi.pamirs.eip.api.service.EipSceneInstanceExecuteService;
import pro.shushi.pamirs.eip.api.service.model.EipIncUpdateCursorService;
import pro.shushi.pamirs.eip.api.service.model.EipSceneInstanceService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.Date;

/**
 * @author drome
 * @date 2021/8/25:20 下午
 */
@Slf4j
@Service
@Fun(EipSceneInstanceExecuteService.FUN_NAMESPACE)
public class EipSceneInstanceExecuteServiceImpl implements EipSceneInstanceExecuteService<SuperMap> {

    @Autowired
    private EipExecuteService eipExecuteService;

    @Autowired
    private EipSceneInstanceService eipSceneInstanceService;

    @Autowired
    private EipIncUpdateCursorService eipIncUpdateLogService;

    @Override
    @Function
    public EipResult<SuperMap> callByCodeNoBody(String interfaceName) {
        return callByCode(interfaceName, null, null);
    }

    @Override
    @Function
    public EipResult<SuperMap> callByCodeAndBody(String interfaceName, Object body) {
        return callByCode(interfaceName, null, body);
    }

    @Override
    @Function
    public EipResult<SuperMap> callByCode(String sceneInstanceCode, SuperMap executorContext, Object body) {
        EipSceneInstance sceneInstance = eipSceneInstanceService.queryByCode(sceneInstanceCode);
        if (sceneInstance == null) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_EXECUTE_ERRPR_CODE_NOMATCH).errThrow();
        }
        if (StringUtils.isEmpty(sceneInstance.getRouteInterfaceName())) {
            throw PamirsException.construct(EipExpEnumerate.SCENE_INSTANCE_EXECUTE_ERRPR_NOREL_ROUTE).errThrow();
        }

        return _call(sceneInstance, executorContext, body);
    }

    public EipResult<SuperMap> _call(EipSceneInstance sceneInstance, SuperMap executorContext, Object body) {
        String routeInterfaceName = sceneInstance.getRouteInterfaceName();
        Boolean useIncUpdateLog = sceneInstance.getUseIncUpdateLog();

        //获取增量日志
        if (Boolean.TRUE.equals(useIncUpdateLog)) {
            //前端触发可以直接指定,但是没有id
            EipIncUpdateCursor eipIncUpdateLog = (EipIncUpdateCursor) executorContext.getIteration(EipSceneConstant.INSTANCE_INC_UPDATE_LOG);
            if (eipIncUpdateLog == null) {
                //从数据库获取
                eipIncUpdateLog = eipIncUpdateLogService.fetchIncUpdateLog(sceneInstance.getIncUpdateLogInterfaceName(), sceneInstance.getDefaultStartTime());
                executorContext.putIteration(EipSceneConstant.INSTANCE_INC_UPDATE_LOG, eipIncUpdateLog);
            }
        }

        //调用eip
        // TODO: 2021/8/9 这里是否应该加事务
        EipResult<SuperMap> result = eipExecuteService.callByInterfaceName(routeInterfaceName, executorContext, body);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            //执行失败, 直接结束
            return result;
        }
        //处理增量
        if (Boolean.TRUE.equals(useIncUpdateLog)) {
            EipIncUpdateCursor incUpdateLog = (EipIncUpdateCursor) executorContext.getIteration(EipSceneConstant.INSTANCE_INC_UPDATE_LOG);
            if (incUpdateLog != null && incUpdateLog.getId() != null) {
                Boolean hasNext = (Boolean) executorContext.getIteration(EipSceneConstant.INSTANCE_CYCLE);
                if (!Boolean.TRUE.equals(hasNext)) {
                    //没有下一页了,结束
                    incUpdateLog.setStartTime(incUpdateLog.getEndTime());
                    incUpdateLog.setEndTime(null);
                    incUpdateLog.setParams(null);
                }
                incUpdateLog.setLastUpdateTime(new Date());
                //更新增量日志
                incUpdateLog.updateById();
            }
        }

        //分页拉取数据的场景,重复执行
        Boolean hasNext = (Boolean) executorContext.getIteration(EipSceneConstant.INSTANCE_CYCLE);
        if (Boolean.TRUE.equals(hasNext)) {
            // body内部自带转String,所以这里不需要处理. 上下文现在希望能复用,所以也不做处理
            //再次循环,且上下文对象不变的情况下,把循环变量置空,避免死循环
            executorContext.putIteration(EipSceneConstant.INSTANCE_CYCLE, null);
            return _call(sceneInstance, executorContext, body);
        }
        return result;
    }

}
