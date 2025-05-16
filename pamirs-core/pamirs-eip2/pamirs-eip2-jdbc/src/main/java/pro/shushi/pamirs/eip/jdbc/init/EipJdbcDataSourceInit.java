package pro.shushi.pamirs.eip.jdbc.init;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.eip.api.config.EipSwitchCondition;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.api.enmu.connector.TestConnStatus;
import pro.shushi.pamirs.eip.jdbc.helper.EipConnectorHelper;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.util.List;

/**
 * JDBC数据源初始化
 *
 * @author Adamancy Zhang at 14:18 on 2024-06-05
 */
@Slf4j
@Order(88)
@Component
@Conditional(EipSwitchCondition.class)
public class EipJdbcDataSourceInit implements SystemBootAfterInit {

    @Override
    public boolean init(AppLifecycleCommand command) {
        initConnector();
        return true;
    }

    @Override
    public int priority() {
        return 88;
    }

    private void initConnector() {
        List<EipConnector> connectors = queryAllValidDbConnectors();
        if (CollectionUtils.isEmpty(connectors)) {
            log.info("camel data source is not initialized. cause: connector count is zero.");
            return;
        }
        for (EipConnector connector : connectors) {
            try {
                EipConnectorHelper.initConnector(connector);
            } catch (Throwable e) {
                log.error("init connector data source error.", e);
            }
        }
        log.info("connector data source is initialized. {}", connectors.size());
    }

    private List<EipConnector> queryAllValidDbConnectors() {
        return new EipConnector().queryList(Pops.<EipConnector>lambdaQuery()
                .from(EipConnector.MODEL_MODEL)
                .eq(EipConnector::getType, ConnType.DB)
                .eq(EipConnector::getTestConnStatus, TestConnStatus.CONN_SUCCESS));
    }
}
