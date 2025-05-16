package pro.shushi.pamirs.expression;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.base.PamirsModule;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;

@Component
@Base
@Boot
@Module(
        name = ExpressionModule.MODULE_NAME,
        displayName = "表达式",
        version = "5.0.0",
        dependencies = {ModuleConstants.MODULE_BASE},
        exclusions = {}
)
@Module.module(ExpressionModule.MODULE_MODULE)
@Module.Advanced(selfBuilt = true, application = false)
public class ExpressionModule implements PamirsModule {

    public static final String MODULE_MODULE = "expression";

    public static final String MODULE_NAME = "expression";

    @Override
    public String[] packagePrefix() {
        return new String[]{
                "pro.shushi.pamirs.expression"
        };
    }

}
