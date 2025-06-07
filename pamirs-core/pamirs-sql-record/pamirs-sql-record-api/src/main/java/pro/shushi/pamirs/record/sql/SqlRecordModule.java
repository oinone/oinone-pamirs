package pro.shushi.pamirs.record.sql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.PamirsModule;

/**
 * SqlRecordModule
 *
 * @author yakir on 2023/06/28 18:27.
 */
@Slf4j
@Base
@Component
@Module(
        name = SqlRecordModule.MODULE_NAME,
        displayName = "SDC",
        version = "4.1.0"
)
@Module.module(SqlRecordModule.MODULE_MODULE)
@Module.Advanced(
        selfBuilt = true,
        application = false
)
public class SqlRecordModule implements PamirsModule {

    public static final String MODULE_MODULE = "sql_record";

    public static final String MODULE_NAME = "sqlRecord";

    @Override
    public String[] packagePrefix() {

        log.info("Pamirs SQL Record .....");

        return new String[]{
                "pro.shushi.pamirs.record.sql"
        };
    }
}
