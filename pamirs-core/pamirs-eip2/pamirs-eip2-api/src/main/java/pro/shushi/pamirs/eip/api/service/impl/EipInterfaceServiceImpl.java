package pro.shushi.pamirs.eip.api.service.impl;

import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipOpenInterface;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.enmu.ComponentTypeEnum;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.*;
import pro.shushi.pamirs.eip.api.pamirs.DefaultConverterFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultFilterFunction;
import pro.shushi.pamirs.eip.api.service.EipInterfaceService;
import pro.shushi.pamirs.eip.api.service.EipLogStrategyService;
import pro.shushi.pamirs.eip.api.util.*;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;

import javax.annotation.Resource;
import java.util.List;

@SuppressWarnings("rawtypes")
@Slf4j
@Service
public class EipInterfaceServiceImpl implements EipInterfaceService {

    @Resource
    private EipLogStrategyService eipLogStrategyService;

    @Override
    public Result<String> registerApi(IEipApi eipApi) {
        if (eipApi instanceof IEipOpenInterface) {
            return registerOpenInterface((IEipOpenInterface) eipApi);
        } else if (eipApi instanceof IEipIntegrationInterface) {
            return registerInterface((IEipIntegrationInterface) eipApi);
        } else if (eipApi instanceof EipRouteDefinition) {
            return registerRouteDefinition((EipRouteDefinition) eipApi);
        } else {
            throw new UnsupportedOperationException("不支持的接口类型");
        }
    }

    @Override
    public Result<String> cancellationApi(IEipApi eipApi) {
        if (eipApi instanceof IEipOpenInterface) {
            return cancellationOpenInterface((IEipOpenInterface) eipApi);
        } else if (eipApi instanceof IEipIntegrationInterface) {
            return cancellationInterface((IEipIntegrationInterface) eipApi);
        } else if (eipApi instanceof EipRouteDefinition) {
            return cancellationRouteDefinition((EipRouteDefinition) eipApi);
        } else {
            throw new UnsupportedOperationException("不支持的接口类型");
        }
    }

    @Override
    public Result<String> registerInterface(IEipIntegrationInterface eipInterface) {
        try {
            EipCamelContext context = EipCamelContext.getContext();
            String interfaceName = eipInterface.getInterfaceName();
            if (eipInterface instanceof EipIntegrationInterface) {
                EipIntegrationInterface integrationInterface = (EipIntegrationInterface) eipInterface;
                if (eipInterface.getIsIgnoreLogFrequency() == null) {
                    Boolean isIgnoreLogFrequency = queryIgnoreFrequency(interfaceName, InterfaceTypeEnum.INTEGRATION);
                    integrationInterface.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
                }
                EipHelper.fillEipIntegrationInterface(integrationInterface);
                integrationInterface.setContext(context);
            }
            EipInterfaceContext.putInterface(eipInterface);
            EipInitializationUtil.newInstance(context)
                    .from(EipFunctionConstant.EMPTY.apply(context, interfaceName))
                    .<EipCamelRouteUtil>to(eipInterface)
                    .end();
        } catch (Exception e) {
            log.error("集成接口注册失败", e);
            return new Result<String>().error().setData(e.getMessage());
        }
        return new Result<>();
    }

    @Override
    public Result<String> cancellationInterface(IEipIntegrationInterface eipInterface) {

        try {
            RouteDefinition routeDefinition = EipInitializationUtil.newInstance()
                    .removeIntegrationInterface(eipInterface.getInterfaceName());
            if (routeDefinition != null) {
                String interfaceName = eipInterface.getInterfaceName();
                EipInterfaceContext.removeInterface(interfaceName);
                EipInterfaceContext.removeTemporaryInterface(interfaceName);
            }
        } catch (Exception e) {
            log.error("集成接口注销失败", e);
            return new Result<String>().error().setData(e.getMessage());
        }
        return new Result<>();
    }

    @Override
    public Result<String> registerRouteDefinition(EipRouteDefinition eipInterface) {
        List<EipComponentDefinition> definitions = eipInterface.getDefinitions();
        if (CollectionUtils.isEmpty(definitions)) {
            return new Result<String>().error().setData(String.format("路由定义中无组件定义，已自动忽略 [interfaceName %s]", eipInterface.getInterfaceName()));
        }
        if (eipInterface.getIsIgnoreLogFrequency() == null) {
            String interfaceName = eipInterface.getInterfaceName();
            Boolean isIgnoreLogFrequency = queryIgnoreFrequency(interfaceName, InterfaceTypeEnum.ROUTE);
            eipInterface.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
        }
        EipCamelContext context = EipCamelContext.getContext();
        EipInitializationUtil util = EipInitializationUtil.newInstance(context);
        EipCamelRouteUtil routeUtil = util.from(EipFunctionConstant.EMPTY.apply(context, eipInterface.getInterfaceName()));
        for (EipComponentDefinition component : definitions) {
            Result<String> result = registerComponent(eipInterface, component, routeUtil);
            if (!result.isSuccess()) {
                return result;
            }
        }
        routeUtil.end();
        return new Result<>();
    }

    private Result<String> registerComponent(EipRouteDefinition eipInterface, EipComponentDefinition component, EipCamelRouteBaseUtil routeUtil) {
        String interfaceName = eipInterface.getInterfaceName();
        ComponentTypeEnum type = component.getType();
        if (type == null) {
            return new Result<String>().error().setData(String.format("组件定义未指定类型，已自动忽略 [interfaceName %s]", interfaceName));
        }
        //注册组件节点
        routeUtil.component(eipInterface, component);

        switch (component.getType()) {
            case NORMAL:
                String integrationInterfaceName = component.getInterfaceName();
                if (StringUtils.isBlank(integrationInterfaceName)) {
                    return new Result<String>().error().setData(String.format("组件定义未提供接口名称，已自动忽略 [interfaceName %s]", interfaceName));
                }
                routeUtil.to(integrationInterfaceName);
                break;
            case FILTER:
                String filterNamespace = component.getFilterNamespace();
                String filterFun = component.getFilterFun();
                List<EipComponentDefinition> filterComponentDefinitions = component.getFilterComponentDefinitions();
                if (StringUtils.isBlank(filterNamespace)) {
                    return new Result<String>().error().setData(String.format("条件组件定义未提供过滤函数的命名空间，已自动忽略 [interfaceName %s]", interfaceName));
                }
                if (StringUtils.isBlank(filterFun)) {
                    return new Result<String>().error().setData(String.format("条件组件定义未提供过滤函数的技术名称，已自动忽略 [interfaceName %s]", interfaceName));
                }
                if (CollectionUtils.isEmpty(filterComponentDefinitions)) {
                    return new Result<String>().error().setData(String.format("条件组件定义未提供后续组件列表，已自动忽略 [interfaceName %s]", interfaceName));
                }
                EipCamelRouteFilterUtil filterUtil = routeUtil.filter(new DefaultFilterFunction<>(component.getFilterNamespace(), component.getFilterFun()));
                //此时已经获取了FilterDefinition,继续注册filter成立后的行为
                for (EipComponentDefinition filterComponent : filterComponentDefinitions) {
                    registerComponent(eipInterface, filterComponent, filterUtil);
                }
                break;
            case FUNCTION:
                String convertNamespace = component.getConvertNamespace();
                String convertFun = component.getConvertFun();
                if (StringUtils.isBlank(convertNamespace)) {
                    return new Result<String>().error().setData(String.format("函数组件定义未提供处理函数的命名空间，已自动忽略 [interfaceName %s]", interfaceName));
                }
                if (StringUtils.isBlank(convertFun)) {
                    return new Result<String>().error().setData(String.format("函数组件定义未提供处理函数的技术名称，已自动忽略 [interfaceName %s]", interfaceName));
                }
                routeUtil.convert(new DefaultConverterFunction<>(convertNamespace, convertFun));
                break;
            case PARAM_PROCESSOR:
                EipParamProcessor paramProcessor = component.getParamProcessor();
                if (paramProcessor == null) {
                    return new Result<String>().error().setData(String.format("参数转换组件定义未提供转换器，已自动忽略 [interfaceName %s]", interfaceName));
                }
                routeUtil.paramProcessor(paramProcessor);
                break;
            default:
                return new Result<String>().error().setData(String.format("未知的组件类型，已自动忽略 [interfaceName %s]", interfaceName));
        }
        return new Result<>();
    }

    @Override
    public Result<String> cancellationRouteDefinition(EipRouteDefinition eipInterface) {
        EipCamelContext context = EipCamelContext.getContext();
        ModelCamelContext camelContext = context.getCamelContext();
        RouteDefinition routeDefinition = camelContext.getRouteDefinition(eipInterface.getInterfaceName());
        if (routeDefinition != null) {
            try {
                camelContext.removeRouteDefinition(routeDefinition);
            } catch (Exception e) {
                log.error("路由定义注销失败", e);
                return new Result<String>().error().setData(e.getMessage());
            }
        }
        return new Result<>();
    }

    @Override
    public Result<String> registerOpenInterface(IEipOpenInterface eipInterface) {
        try {
            EipCamelContext context = EipCamelContext.getContext();
            if (eipInterface instanceof EipOpenInterface) {
                EipOpenInterface openInterface = (EipOpenInterface) eipInterface;
                if (eipInterface.getIsIgnoreLogFrequency() == null) {
                    String interfaceName = openInterface.getInterfaceName();
                    Boolean isIgnoreLogFrequency = queryIgnoreFrequency(interfaceName, InterfaceTypeEnum.OPEN);
                    openInterface.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
                }
                EipHelper.fillEipOpenInterface(openInterface);
                openInterface.setContext(context);
            }
            EipInitializationUtil.newInstance(context)
                    .addOpenApi(eipInterface);
        } catch (Exception e) {
            log.error("开放接口注册失败", e);
            return new Result<String>().error().setData(e.getMessage());
        }
        return new Result<>();
    }

    @Override
    public Result<String> cancellationOpenInterface(IEipOpenInterface eipInterface) {
        try {
            EipInitializationUtil.newInstance()
                    .removeOpenApi(eipInterface.getInterfaceName());
        } catch (Exception e) {
            log.error("开放接口注销失败", e);
            return new Result<String>().error().setData(e.getMessage());
        }
        return new Result<>();
    }

    /**
     * 从DB获取忽略日志频率配置
     */
    private Boolean queryIgnoreFrequency(String interfaceName, InterfaceTypeEnum interfaceTypeEnum) {
        Boolean isIgnoreLogFrequency = eipLogStrategyService.queryIgnoreFrequency(interfaceName, InterfaceTypeEnum.INTEGRATION);
        log.warn("缺少属性isIgnoreLogFrequency,interfaceName:{},isIgnoreLogFrequency:{}", interfaceName, isIgnoreLogFrequency);
        return isIgnoreLogFrequency;
    }
}
