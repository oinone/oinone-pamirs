package pro.shushi.pamirs.framework.connectors.data.dialect.condition;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

import java.util.Map;

/**
 * 方言表达式
 *
 * @author Adamancy Zhang at 16:17 on 2026-02-26
 */
public abstract class AbstractDialectCondition implements Condition {

    protected abstract DataSourceEnum getDataSource();

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        Binder binder = Binder.get(environment);
        String type = getDataSource().value();
        Map<String, DialectDs> dialectDsMap = binder.bind(ConfigureConstants.DIALECT_DATASOURCE_PREFIX, Bindable.mapOf(String.class, DialectDs.class)).orElse(Map.of());
        for (DialectDs dialectDs : dialectDsMap.values()) {
            if (type.equals(dialectDs.getType())) {
                return true;
            }
        }
        return false;
    }

    static class DialectDs {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
