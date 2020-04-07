package pro.shushi.pamirs.meta.api;

import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.ExtPointApi;

/**
 * 扩展点api快捷方式
 *
 * @author d
 * @version 2019-04-26
 */
@Slf4j
public class Ext<R> implements ExtPointApi<R> {

    public static <R> Ext<R> get() {
        return new Ext<>();
    }

    @Override
    public R run(String extPointName, Object... args) {
        return (R)MetaApiFactory.getApi(ExtPointApi.class).run(extPointName, args);
    }

    @Override
    public R runDefault(String extPointName, Object... args) {
        return (R)MetaApiFactory.getApi(ExtPointApi.class).runDefault(extPointName, args);
    }

}
