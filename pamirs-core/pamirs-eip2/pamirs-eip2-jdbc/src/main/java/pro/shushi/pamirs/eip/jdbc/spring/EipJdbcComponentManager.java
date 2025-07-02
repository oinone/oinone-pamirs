package pro.shushi.pamirs.eip.jdbc.spring;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.jdbc.service.EipJdbcComponent;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.stl.ConcurrentHashSet;

import java.util.Set;

/**
 * EipJdbcComponentManager
 *
 * @author yakir on 2025/06/17 14:32.
 */
@Slf4j
public class EipJdbcComponentManager {

    private static final Set<EipJdbcComponent> jdbcComponents = new ConcurrentHashSet<>();

    public static void register(EipJdbcComponent jdbcComponent) {
        if (null == jdbcComponent) {
            return;
        }
        log.info("Jdbc Url Component [{}] Register", jdbcComponent.dbType());
        jdbcComponents.add(jdbcComponent);
    }

    public static EipJdbcComponent get(String dbType) {
        for (EipJdbcComponent jdbcComponent : jdbcComponents) {
            if (StringUtils.equalsIgnoreCase(jdbcComponent.dbType(), dbType)) {
                return jdbcComponent;
            }
        }
        throw PamirsException.construct(EipExpEnumerate.EIP_DB_TYPE_ERROR)
                .appendMsg(dbType)
                .errThrow();
    }
}
