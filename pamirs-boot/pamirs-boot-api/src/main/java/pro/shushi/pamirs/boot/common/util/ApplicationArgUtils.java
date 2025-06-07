package pro.shushi.pamirs.boot.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.boot.common.api.command.AppArgs;
import pro.shushi.pamirs.boot.common.api.command.AppCommand;
import pro.shushi.pamirs.boot.common.api.contants.*;
import pro.shushi.pamirs.meta.common.util.PStringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationArgUtils {

    private static final Logger log = LoggerFactory.getLogger(ApplicationArgUtils.class);

    public static final String PREFIX_P = "-P";

    private static final String P_LIFE_CYCLE = PREFIX_P + "lifecycle";

    private static final String P_BUILD_TABLE = PREFIX_P + "buildTable";

    private static final String P_MODULE_ONLINE = PREFIX_P + "moduleOnline";

    private static final String P_META_ONLINE = PREFIX_P + "metaOnline";

    private static final String P_ENABLE_RPC = PREFIX_P + "enableRpc";

    private static final String P_OPEN_API = PREFIX_P + "openApi";

    private static final String P_CHECK_FIELD = PREFIX_P + "checkField";

    private static final String P_INIT_DATA = PREFIX_P + "initData";

    private static final String P_GO_BACK = PREFIX_P + "goBack";

    private static final AppArgs args = new AppArgs();

    private static AppCommand command;

    public static void handle(String[] args) {

        log.info("Application Arguments Usage:");
        log.info("  {}=INSTALL | RELOAD | CUSTOM_INSTALL | PACKAGE | DDL ", P_LIFE_CYCLE);
        log.info("  {}=NEVER | EXTEND | DIFF ", P_BUILD_TABLE);
        log.info("  {}=NEVER | READ | CHECK ", P_MODULE_ONLINE);
        log.info("  {}=NEVER | MODULE | ALL ", P_META_ONLINE);
        log.info("  {}=true | false ", P_OPEN_API);
        log.info("  {}=true | false ", P_ENABLE_RPC);
        log.info("  {}=true | false ", P_CHECK_FIELD);
        log.info("  {}=true | false ", P_INIT_DATA);
        log.info("  {}=true | false ", P_GO_BACK);
        log.info("Application Arguments {}", Arrays.toString(args));

        Map<String, String> pArgs = Arrays.stream(Optional.ofNullable(args).orElse(new String[]{}))
                .filter(_arg -> _arg.startsWith(PREFIX_P))
                .map(_pArgs -> _pArgs.split("="))
                .filter(v -> v.length == 2)
                .collect(Collectors.toMap(_argArr -> _argArr[0], _argArr -> _argArr[1], (a, b) -> a));

        LifecycleDeployEnum lifeCycle = Optional.ofNullable(pArgs.remove(P_LIFE_CYCLE))
                .map(LifecycleDeployEnum::valueOf)
                .orElse(LifecycleDeployEnum.INSTALL);

        BuildTableEnum buildTable = Optional.ofNullable(pArgs.remove(P_BUILD_TABLE))
                .map(BuildTableEnum::valueOf)
                .orElse(null);

        ModuleOnlineEnum moduleOnline = Optional.ofNullable(pArgs.remove(P_MODULE_ONLINE))
                .map(ModuleOnlineEnum::valueOf)
                .orElse(null);

        MetaOnlineEnum metaOnline = Optional.ofNullable(pArgs.remove(P_META_ONLINE))
                .map(MetaOnlineEnum::valueOf)
                .orElse(null);

        Boolean enableRpc = Optional.ofNullable(pArgs.remove(P_ENABLE_RPC))
                .map(Boolean::valueOf)
                .orElse(null);

        Boolean openApi = Optional.ofNullable(pArgs.remove(P_OPEN_API))
                .map(Boolean::valueOf)
                .orElse(null);

        Boolean checkField = Optional.ofNullable(pArgs.remove(P_CHECK_FIELD))
                .map(Boolean::valueOf)
                .orElse(null);

        Boolean initData = Optional.ofNullable(pArgs.remove(P_INIT_DATA))
                .map(Boolean::valueOf)
                .orElse(null);

        Boolean goBack = Optional.ofNullable(pArgs.remove(P_GO_BACK))
                .map(Boolean::valueOf)
                .orElse(false);

        log.info("Module Lifecycle: {}", PStringUtils.toStringAndNullToEmpty(lifeCycle));
        log.info("Build Table: {}", PStringUtils.toStringAndNullToEmpty(buildTable));
        log.info("Module Online: {}", PStringUtils.toStringAndNullToEmpty(moduleOnline));
        log.info("Meta Online: {}", PStringUtils.toStringAndNullToEmpty(metaOnline));
        log.info("Open Api: {}", PStringUtils.toStringAndNullToEmpty(openApi));
        log.info("Check Field: {}", PStringUtils.toStringAndNullToEmpty(checkField));
        log.info("Init Data: {}", PStringUtils.toStringAndNullToEmpty(initData));
        log.info("Go Back: {}", PStringUtils.toStringAndNullToEmpty(goBack));

        ApplicationArgUtils.args.setLifecycle(lifeCycle);
        ApplicationArgUtils.args.setBuildTable(buildTable);
        ApplicationArgUtils.args.setModuleOnline(moduleOnline);
        ApplicationArgUtils.args.setMetaOnline(metaOnline);
        ApplicationArgUtils.args.setEnableRpc(enableRpc);
        ApplicationArgUtils.args.setOpenApi(openApi);
        ApplicationArgUtils.args.setCheckField(checkField);
        ApplicationArgUtils.args.setInitData(initData);
        ApplicationArgUtils.args.setGoBack(goBack);
        ApplicationArgUtils.args.setArgs(pArgs);

        // 启动模式
        if (LifecycleDeployEnum.RELOAD.equals(lifeCycle)) {
            command = new AppCommand(InstallEnum.READONLY, UpgradeEnum.READONLY, ProfileEnum.READONLY);
        } else if (LifecycleDeployEnum.INSTALL.equals(lifeCycle)) {
            command = new AppCommand(InstallEnum.AUTO, UpgradeEnum.FORCE, ProfileEnum.AUTO);
        } else if (LifecycleDeployEnum.CUSTOM_INSTALL.equals(lifeCycle)) {
            command = new AppCommand(InstallEnum.AUTO, UpgradeEnum.FORCE, ProfileEnum.CUSTOMIZE);
        } else if (LifecycleDeployEnum.PACKAGE.equals(lifeCycle)) {
            command = new AppCommand(InstallEnum.AUTO, UpgradeEnum.FORCE, ProfileEnum.PACKAGE);
        } else if (LifecycleDeployEnum.DDL.equals(lifeCycle)) {
            command = new AppCommand(InstallEnum.AUTO, UpgradeEnum.FORCE, ProfileEnum.DDL);
        }

    }

    public static AppArgs getArgs() {
        return args;
    }

    public static AppCommand getCommand() {
        return command;
    }

}
