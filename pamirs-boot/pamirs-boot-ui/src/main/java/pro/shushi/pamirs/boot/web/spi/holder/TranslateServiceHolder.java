package pro.shushi.pamirs.boot.web.spi.holder;

import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 翻译服务持有者
 *
 * @author Adamancy Zhang at 20:29 on 2024-11-14
 */
public class TranslateServiceHolder {

    private static final HoldKeeper<TranslateService> holder = new HoldKeeper<>();

    public static TranslateService get() {
        return holder.supply(() -> Spider.getDefaultExtension(TranslateService.class));
    }
}
