package pro.shushi.pamirs.framework.connectors.data.datasource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.configure.PamirsFrameworkDataConfiguration;
import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Ds;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import jakarta.annotation.Resource;

import static pro.shushi.pamirs.framework.connectors.data.api.datasource.DsHintApi.expression;

/**
 * 动态数据源切面
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
public class DynamicDataSourceAspect {

    @Resource
    private PamirsFrameworkDataConfiguration pamirsFrameworkDataConfiguration;

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    @Before("@annotation(ds) || @within(ds)")
    public void changeDataSource(JoinPoint point, Ds ds) {
        String dsKeyString = ds.value();
        Object dsKey = dsKeyString;
        if (StringUtils.isBlank(dsKeyString)) {
            String model = ds.model();
            if (StringUtils.isNotBlank(model)) {
                dsKey = pamirsMapperConfiguration.getDataSourceRouteService().route(model);
            }
        }
        if (null == dsKey) {
            dsKey = PamirsSession.getDsKey();
        }
        dsKey = expression(dsKey);
        log.debug("Use datasource (" + (null == dsKey ? "default" : dsKey) + ")-" + point.getSignature());
        PamirsSession.pushDsKey(dsKey);
    }

    @After("@annotation(ds) || @within(ds)")
    public void restoreDataSource(JoinPoint point, Ds ds) {
        log.debug("Restore datasource -" + point.getSignature());
        PamirsSession.clearDsKey();
    }

    @AfterThrowing("@annotation(ds) || @within(ds)")
    public void restoreDataSourceAndSetModel(JoinPoint point, Ds ds) {
        log.debug("Restore datasource -" + point.getSignature());
        PamirsSession.clearDsKey();
    }

}