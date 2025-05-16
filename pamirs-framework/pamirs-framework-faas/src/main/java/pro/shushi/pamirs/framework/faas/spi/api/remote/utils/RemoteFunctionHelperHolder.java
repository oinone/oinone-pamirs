package pro.shushi.pamirs.framework.faas.spi.api.remote.utils;

import pro.shushi.pamirs.framework.faas.spi.api.remote.IRemoteFunctionHelper;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 远程函数帮助类持有者
 *
 * @author Adamancy Zhang at 12:20 on 2024-07-18
 */
public class RemoteFunctionHelperHolder {

    private static final HoldKeeper<IRemoteFunctionHelper> REMOTE_FUNCTION_HELPER_HOLDER = new HoldKeeper<>();

    public static IRemoteFunctionHelper get() {
        return REMOTE_FUNCTION_HELPER_HOLDER.supply(() -> Spider.getDefaultExtension(IRemoteFunctionHelper.class));
    }
}
