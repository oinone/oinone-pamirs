package pro.shushi.pamirs.boot.common.api.command;

import pro.shushi.pamirs.boot.common.api.contants.BuildTableEnum;
import pro.shushi.pamirs.boot.common.api.contants.LifecycleDeployEnum;
import pro.shushi.pamirs.boot.common.api.contants.MetaOnlineEnum;
import pro.shushi.pamirs.boot.common.api.contants.ModuleOnlineEnum;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Map;

/**
 * 应用参数
 * <p>
 * 2021/12/29 4:19 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class AppArgs {

    private LifecycleDeployEnum lifecycle;

    private BuildTableEnum buildTable;

    private ModuleOnlineEnum moduleOnline;

    private MetaOnlineEnum metaOnline;

    private Boolean enableRpc;

    private Boolean openApi;

    private Boolean checkField;

    private Boolean initData;

    private Boolean goBack;

    private Map<String, String> args;
}
