package pro.shushi.pamirs.framework.connectors.data.mapper.aspect;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.TypeUtils;

import jakarta.annotation.Resource;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Mapper获取模型编码切面
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Slf4j
@Aspect
@Order(-10)
@Component
public class MapperAspect {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    @Pointcut("@annotation(pro.shushi.pamirs.framework.connectors.data.mapper.aspect.ModelFill) || @within(pro.shushi.pamirs.framework.connectors.data.mapper.aspect.ModelFill)")
    public void pointcutMapper() {

    }

    @Before("pointcutMapper()")
    public void changeDataSourceAndSetModel(JoinPoint point) {
        Object dsKey = PamirsSession.getDsKey();
        if (null == dsKey) {
            String model = null;
            Class<?> modelClass = getModelClass(point);
            Object[] args = point.getArgs();
            if (ArrayUtils.isNotEmpty(args)) {
                if (null == modelClass || DataMap.class.isAssignableFrom(modelClass)) {
                    model = MapperContext.model(args);
                } else {
                    model = Models.api().getModel(modelClass);
                    MapperContext.setModel(model, args);
                }
            }
            if (StringUtils.isNotBlank(model)) {
                dsKey = pamirsMapperConfiguration.getDataSourceRouteService().route(model);
            }
        }
        if (null == dsKey) {
            dsKey = pamirsFrameworkDataConfiguration.getDefaultDsKey();
        }
        if (log.isDebugEnabled()) {
            log.debug("使用数据源(" + (null == dsKey ? "default" : dsKey) + ")-" + point.getSignature());
        }
        PamirsSession.pushDsKey(dsKey);
    }

    @AfterReturning(value = "pointcutMapper()", returning = "retVal")
    public void restoreDataSourceAndSetModel(JoinPoint point, Object retVal) {
        if (null != retVal) {
            Class<?> modelClass = getModelClass(point);
            Object[] args = point.getArgs();
            String model = null;
            if (ArrayUtils.isNotEmpty(args)) {
                if (null == modelClass || DataMap.class.isAssignableFrom(modelClass)) {
                    model = MapperContext.model(args);
                } else {
                    model = Models.api().getModel(modelClass);
                }
            }
            if (StringUtils.isNotBlank(model)) {
                MapperContext.setModel(model, retVal);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("恢复数据源-" + point.getSignature());
        }
        PamirsSession.clearDsKey();
    }

    @AfterThrowing(value = "pointcutMapper()")
    public void restoreDataSourceAndSetModel(JoinPoint point) {
        if (log.isDebugEnabled()) {
            log.debug("恢复数据源-" + point.getSignature());
        }
        PamirsSession.clearDsKey();
    }

    protected static Class<?> getModelClass(JoinPoint point) {
        Class<?> mapperClass = Optional.of(AopUtils.getTargetClass(point.getTarget()).getGenericInterfaces())
                .filter(ArrayUtils::isNotEmpty).map(v -> v[0]).map(Type::getTypeName).map(TypeUtils::getClass)
                .orElse(null);
        if (null != mapperClass && PamirsMapper.class.isAssignableFrom(mapperClass)) {
            return TypeUtils.extractModelClass(mapperClass);
        }
        return null;
    }

}