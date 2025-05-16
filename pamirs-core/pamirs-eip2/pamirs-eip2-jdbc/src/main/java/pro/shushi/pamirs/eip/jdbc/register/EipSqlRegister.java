package pro.shushi.pamirs.eip.jdbc.register;

import org.apache.camel.component.sql.SqlComponent;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.eip.api.camel.IEipRegister;
import pro.shushi.pamirs.eip.api.camel.RegistryComponentBody;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.jdbc.camel.EipSqlPrepareStatementStrategy;

import java.util.List;

/**
 * SQL组件注册
 *
 * @author Adamancy Zhang at 17:49 on 2024-06-05
 */
@Component
public class EipSqlRegister implements IEipRegister {

    @Override
    public List<RegistryComponentBody> registers() {
        EipCamelContext context = EipCamelContext.getContext();
        SqlComponent sqlComponent = context.getCamelContext().getComponent("sql", SqlComponent.class);
        if (sqlComponent != null) {
            setSqlComponentProperties(sqlComponent);
        }
        return CollectionHelper.<RegistryComponentBody>newInstance()
                .add(new RegistryComponentBody(EipSqlPrepareStatementStrategy.NAME, EipSqlPrepareStatementStrategy.INSTANCE))
                .build();
    }

    private void setSqlComponentProperties(SqlComponent sqlComponent) {
    }
}
