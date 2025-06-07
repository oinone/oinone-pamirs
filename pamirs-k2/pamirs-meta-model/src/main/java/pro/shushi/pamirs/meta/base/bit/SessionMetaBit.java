package pro.shushi.pamirs.meta.base.bit;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveBatchApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 请求上下文指令
 * <p>
 * 2020/7/20 2:01 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unchecked")
public interface SessionMetaBit extends MetaBit {

    default ModelDirectiveBatchApi directive() {
        return Spider.getDefaultExtension(ModelDirectiveBatchApi.class);
    }

    default <T> T sudo() {
        return (T) directive().sudo(this);
    }

    default <T> T disableSudo() {
        return (T) directive().disableSudo(this);
    }

    @JSONField(serialize = false)
    default boolean isSudo() {
        return directive().isSudo(this);
    }

    default <T> T disableOptimisticLocker() {
        return (T) directive().disableOptimisticLocker(this);
    }

    default <T> T enableOptimisticLocker() {
        return (T) directive().enableOptimisticLocker(this);
    }

    @JSONField(serialize = false)
    default boolean isOptimisticLocker() {
        return directive().isOptimisticLocker(this);
    }

    @SuppressWarnings("UnusedReturnValue")
    default <T> T enableCheck() {
        return (T) directive().enableCheck(this);
    }

    default <T> T disableCheck() {
        return (T) directive().disableCheck(this);
    }

    @JSONField(serialize = false)
    default boolean isDoCheck() {
        return directive().isDoCheck(this);
    }

    default <T> T enableDefaultValue() {
        return (T) directive().enableDefaultValue(this);
    }

    default <T> T disableDefaultValue() {
        return (T) directive().disableDefaultValue(this);
    }

    @JSONField(serialize = false)
    default boolean isDoDefaultValue() {
        return directive().isDoDefaultValue(this);
    }

    default <T> T enableHook() {
        return (T) directive().enableHook(this);
    }

    @SuppressWarnings("unused")
    default <T> T disableHook() {
        return (T) directive().disableHook(this);
    }

    @JSONField(serialize = false)
    default boolean isHook() {
        return directive().isHook(this);
    }

    default <T> T enableExtPoint() {
        return (T) directive().enableExtPoint(this);
    }

    default <T> T disableExtPoint() {
        return (T) directive().disableExtPoint(this);
    }

    @JSONField(serialize = false)
    default boolean isDoExtPoint() {
        return directive().isDoExtPoint(this);
    }

    @SuppressWarnings("UnusedReturnValue")
    default <T> T enableUsePkStrategy() {
        return (T) directive().enableUsePkStrategy(this);
    }

    @SuppressWarnings("UnusedReturnValue")
    default <T> T disableUsePkStrategy() {
        return (T) directive().disableUsePkStrategy(this);
    }

    @JSONField(serialize = false)
    default boolean isUsePkStrategy() {
        return directive().isUsePkStrategy(this);
    }

    default <T> T enableBuiltAction() {
        return (T) directive().enableBuiltAction(this);
    }

    default <T> T disableBuiltAction() {
        return (T) directive().disableBuiltAction(this);
    }

    @JSONField(serialize = false)
    default boolean isBuiltAction() {
        return directive().isBuiltAction(this);
    }

    default <T> T enableFromClient() {
        return (T) directive().enableFromClient(this);
    }

    default <T> T disableFromClient() {
        return (T) directive().disableFromClient(this);
    }

    @JSONField(serialize = false)
    default boolean isFromClient() {
        return directive().isFromClient(this);
    }

    default <T> T enableIgnoreFunManagement() {
        return (T) directive().enableIgnoreFunManagement(this);
    }

    default <T> T disableIgnoreFunManagement() {
        return (T) directive().disableIgnoreFunManagement(this);
    }

    @JSONField(serialize = false)
    default boolean isIgnoreFunManagement() {
        return directive().isIgnoreFunManagement(this);
    }

    default <T> T enableRemoteMeta() {
        return (T) directive().enableRemoteMeta(this);
    }

    default <T> T disableRemoteMeta() {
        return (T) directive().disableRemoteMeta(this);
    }

    @JSONField(serialize = false)
    default boolean isRemoteMeta() {
        return directive().isRemoteMeta(this);
    }

}
