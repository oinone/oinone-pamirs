package pro.shushi.pamirs.framework.connectors.data.holder;

import pro.shushi.pamirs.framework.connectors.data.mapper.batch.PamirsBatchApi;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 批量提交API持有者
 *
 * @author Adamancy Zhang at 16:57 on 2024-10-18
 */
public class PamirsBatchApiHolder {

    public static HoldKeeper<PamirsBatchApi> holder = new HoldKeeper<>();

    public static PamirsBatchApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(PamirsBatchApi.class));
    }
}
