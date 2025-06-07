package pro.shushi.pamirs.boot.common.api.command;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.common.api.contants.*;

/**
 * 生命周期管理指令
 * <p>
 * 2021/2/25 1:50 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AppLifecycleCommand extends AppCommand {

    private static final AppLifecycleOptions autoOptions = new AppLifecycleOptions();

    private static final AppLifecycleOptions readonlyOptions = new AppLifecycleOptions();

    private static final AppLifecycleOptions packageOptions = new AppLifecycleOptions();

    private static final AppLifecycleOptions ddlOptions = new AppLifecycleOptions();

    static {

        autoOptions.setReloadModule(true);
        autoOptions.setCheckModule(true);
        autoOptions.setReloadMeta(true);
        autoOptions.setDiffMeta(true);
        autoOptions.setDiffTable(true);
        autoOptions.setUpdateModule(true);
        autoOptions.setUpdateMeta(true);

        readonlyOptions.setReloadModule(true);
        readonlyOptions.setCheckModule(true);
        readonlyOptions.setLoadMeta(false);
        readonlyOptions.setReloadMeta(true);
        readonlyOptions.setComputeMeta(false);
        readonlyOptions.setEditMeta(false);
        readonlyOptions.setDiffMeta(false);
        readonlyOptions.setRebuildTable(false);
        readonlyOptions.setUpdateData(false);

        packageOptions.setReloadModule(true);
        packageOptions.setCheckModule(true);
        packageOptions.setReloadMeta(true);
        packageOptions.setDiffMeta(true);
        packageOptions.setRebuildHttpApi(false);
        packageOptions.setDiffTable(true);
        packageOptions.setPublishService(false);
        packageOptions.setUpdateModule(true);
        packageOptions.setUpdateMeta(true);

        ddlOptions.setReloadModule(true);
        ddlOptions.setCheckModule(true);
        ddlOptions.setReloadMeta(true);
        ddlOptions.setRebuildHttpApi(false);
        ddlOptions.setRebuildTable(false);
        ddlOptions.setPrintDDL(true);
        ddlOptions.setPublishService(false);
        ddlOptions.setUpdateData(false);

    }

    private static final long serialVersionUID = 6486402817560483768L;

    // 可选项
    @JSONField(ordinal = 4)
    private AppLifecycleOptions options;

    private AppLifecycleCommand(InstallEnum installEnum, UpgradeEnum upgradeEnum, ProfileEnum profile) {
        super(installEnum, upgradeEnum, profile);
    }

    public static AppLifecycleCommand init(InstallEnum installEnum, UpgradeEnum upgradeEnum, ProfileEnum profile,
                                           AppLifecycleOptions options) {
        AppLifecycleCommand appLifecycleCommand = new AppLifecycleCommand(installEnum, upgradeEnum, profile);

        if (null == options) {
            options = new AppLifecycleOptions();
        }

        if (ProfileEnum.CUSTOMIZE.equals(profile)) {
            appLifecycleCommand.options = options;
        } else {
            if (ProfileEnum.AUTO.equals(profile)) {
                appLifecycleCommand.options = AppLifecycleCommand.autoOptions.deepClone();
            } else if (ProfileEnum.READONLY.equals(profile)) {
                appLifecycleCommand.options = AppLifecycleCommand.readonlyOptions.deepClone();
            } else if (ProfileEnum.PACKAGE.equals(profile)) {
                appLifecycleCommand.options = AppLifecycleCommand.packageOptions.deepClone();
            } else if (ProfileEnum.DDL.equals(profile)) {
                appLifecycleCommand.options = AppLifecycleCommand.ddlOptions.deepClone();
            } else {
                appLifecycleCommand.options = options;
            }
            appLifecycleCommand.options.setParams(options.getParams());
        }
        return appLifecycleCommand;
    }

    public AppLifecycleCommand config(AppArgs appArgs) {
        if (BuildTableEnum.NEVER.equals(appArgs.getBuildTable())) {
            getOptions().setDiffTable(false);
            getOptions().setRebuildTable(false);
        } else if (BuildTableEnum.EXTEND.equals(appArgs.getBuildTable())) {
            getOptions().setDiffTable(false);
            getOptions().setRebuildTable(true);
        } else if (BuildTableEnum.DIFF.equals(appArgs.getBuildTable())) {
            getOptions().setDiffTable(true);
            getOptions().setRebuildTable(true);
        }

        if (ModuleOnlineEnum.NEVER.equals(appArgs.getModuleOnline())) {
            getOptions().setReloadModule(false);
            getOptions().setCheckModule(false);
        } else if (ModuleOnlineEnum.READ.equals(appArgs.getModuleOnline())) {
            getOptions().setReloadModule(true);
            getOptions().setCheckModule(false);
        } else if (ModuleOnlineEnum.CHECK.equals(appArgs.getModuleOnline())) {
            getOptions().setReloadModule(true);
            getOptions().setCheckModule(true);
        }

        if (MetaOnlineEnum.NEVER.equals(appArgs.getMetaOnline())) {
            getOptions().setUpdateModule(false);
            getOptions().setReloadMeta(true);
            getOptions().setUpdateMeta(false);
        } else if (MetaOnlineEnum.READ.equals(appArgs.getMetaOnline())) {
            getOptions().setUpdateModule(false);
            getOptions().setReloadMeta(true);
            getOptions().setUpdateMeta(false);
        } else if (MetaOnlineEnum.REGISTER_MODULE.equals(appArgs.getMetaOnline())) {
            getOptions().setUpdateModule(true);
            getOptions().setReloadMeta(false);
            getOptions().setUpdateMeta(false);
        } else if (MetaOnlineEnum.ALL.equals(appArgs.getMetaOnline())) {
            getOptions().setUpdateModule(true);
            getOptions().setReloadMeta(true);
            getOptions().setUpdateMeta(true);
        }

        if (null != appArgs.getEnableRpc()) {
            getOptions().setPublishService(appArgs.getEnableRpc());
        }

        if (null != appArgs.getOpenApi()) {
            getOptions().setRebuildHttpApi(appArgs.getOpenApi());
        }

        if (null != appArgs.getInitData()) {
            getOptions().setUpdateData(appArgs.getInitData());
        }

        if (null != appArgs.getGoBack()) {
            getOptions().setGoBack(appArgs.getGoBack());
        }
        return this;
    }

    public AppLifecycleOptions getOptions() {
        return options;
    }

}
