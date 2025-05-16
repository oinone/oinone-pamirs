package pro.shushi.pamirs.meta.api.core.orm.systems.directive;

import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveBatchApi;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 指令
 * <p>
 * 2020/7/13 3:17 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service
public class DefaultModelDirectiveApi implements ModelDirectiveApi {

    private static final HoldKeeper<ModelDirectiveBatchApi> holder = new HoldKeeper<>();

    private static ModelDirectiveBatchApi getApi() {
        return holder.supply(() -> Spider.getDefaultExtension(ModelDirectiveBatchApi.class));
    }

    @Override
    public <T> T clear(T listOrObject) {
        return getApi().clear(listOrObject);
    }

    @Override
    public <T> T clearAll(T listOrObject) {
        String model = Models.api().getModel(listOrObject);
        DataComputeTemplate.getInstance().compute(model, listOrObject,
                (lModel, origin) -> this.clearAll(origin),
                (lModel, origin) -> this.clear(origin),
                (lModel, origin) -> listOrObject,
                (context, fieldConfig, dMap) -> {
                    if (0L == this.value(dMap)) {
                        return;
                    }
                    this.clearAll(dMap.get(fieldConfig.getLname()));
                });
        return listOrObject;
    }

    @Override
    public <T> Long value(T obj) {
        return getApi().value(obj);
    }

    @Override
    public <T> T enableColumn(T listOrObject) {
        return getApi().enableColumn(listOrObject);
    }

    @Override
    public <T> T disableColumn(T listOrObject) {
        return getApi().disableColumn(listOrObject);
    }

    @Override
    public <T> boolean isDoColumn(T listOrObject) {
        return getApi().isDoColumn(listOrObject);
    }

    @Override
    public <T> T enableReentry(T listOrObject) {
        return getApi().enableReentry(listOrObject);
    }

    @Override
    public <T> T disableReentry(T listOrObject) {
        return getApi().disableReentry(listOrObject);
    }

    @Override
    public <T> boolean isReentry(T listOrObject) {
        return getApi().isReentry(listOrObject);
    }

    @Override
    public <T> T enableOrmReentry(T listOrObject) {
        return getApi().enableOrmReentry(listOrObject);
    }

    @Override
    public <T> T disableOrmReentry(T listOrObject) {
        return getApi().disableOrmReentry(listOrObject);
    }

    @Override
    public <T> boolean isOrmReentry(T listOrObject) {
        return getApi().isOrmReentry(listOrObject);
    }

    @Override
    public <T> T enableDirty(T listOrObject) {
        return getApi().enableDirty(listOrObject);
    }

    @Override
    public <T> T disableDirty(T listOrObject) {
        return getApi().disableDirty(listOrObject);
    }

    @Override
    public <T> boolean isDirty(T listOrObject) {
        return getApi().isDirty(listOrObject);
    }

    @Override
    public <T> T enableMetaCompleted(T listOrObject) {
        return getApi().enableMetaCompleted(listOrObject);
    }

    @Override
    public <T> T disableMetaCompleted(T listOrObject) {
        return getApi().disableMetaCompleted(listOrObject);
    }

    @Override
    public <T> boolean isMetaCompleted(T listOrObject) {
        return getApi().isMetaCompleted(listOrObject);
    }

    @Override
    public <T> T enableMetaInherited(T listOrObject) {
        return getApi().enableMetaInherited(listOrObject);
    }

    @Override
    public <T> T disableMetaInherited(T listOrObject) {
        return getApi().disableMetaInherited(listOrObject);
    }

    @Override
    public <T> boolean isMetaInherited(T listOrObject) {
        return getApi().isMetaInherited(listOrObject);
    }

    @Override
    public <T> T enableMetaDiffing(T listOrObject) {
        return getApi().enableMetaDiffing(listOrObject);
    }

    @Override
    public <T> T disableMetaDiffing(T listOrObject) {
        return getApi().disableMetaDiffing(listOrObject);
    }

    @Override
    public <T> boolean isMetaDiffing(T listOrObject) {
        return getApi().isMetaDiffing(listOrObject);
    }

    @Override
    public <T> T enableMetaCrossing(T listOrObject) {
        return getApi().enableMetaCrossing(listOrObject);
    }

    @Override
    public <T> T disableMetaCrossing(T listOrObject) {
        return getApi().disableMetaCrossing(listOrObject);
    }

    @Override
    public <T> boolean isMetaCrossing(T listOrObject) {
        return getApi().isMetaCrossing(listOrObject);
    }

    @Override
    public <T> T enableDefaultValue(T listOrObject) {
        return getApi().enableDefaultValue(listOrObject);
    }

    @Override
    public <T> T disableDefaultValue(T listOrObject) {
        return getApi().disableDefaultValue(listOrObject);
    }

    @Override
    public <T> boolean isDoDefaultValue(T listOrObject) {
        return getApi().isDoDefaultValue(listOrObject);
    }

}
