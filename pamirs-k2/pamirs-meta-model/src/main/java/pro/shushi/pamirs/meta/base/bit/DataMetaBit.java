package pro.shushi.pamirs.meta.base.bit;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.meta.api.Models;

/**
 * 数据指令管理
 * <p>
 * 2020/7/20 2:01 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unchecked")
public interface DataMetaBit extends MetaBit {

    default <T extends DataMetaBit> T enableReentry() {
        return (T) Models.modelDirective().enableReentry(this);
    }

    default <T extends DataMetaBit> T disableReentry() {
        return (T) Models.modelDirective().disableReentry(this);
    }

    @JSONField(serialize = false)
    default boolean isReentry() {
        return Models.modelDirective().isReentry(this);
    }

    default <T extends DataMetaBit> T enableDirty() {
        return (T) Models.modelDirective().enableDirty(this);
    }

    default <T extends DataMetaBit> T disableDirty() {
        return (T) Models.modelDirective().disableDirty(this);
    }

    @JSONField(serialize = false)
    default boolean isDirty() {
        return Models.modelDirective().isDirty(this);
    }

    default <T extends DataMetaBit> T enableMetaInherited() {
        return (T) Models.modelDirective().enableMetaInherited(this);
    }

    default <T extends DataMetaBit> T disableMetaInherited() {
        return (T) Models.modelDirective().disableMetaInherited(this);
    }

    @JSONField(serialize = false)
    default boolean isMetaInherited() {
        return Models.modelDirective().isMetaInherited(this);
    }

    default <T extends DataMetaBit> T enableMetaCompleted() {
        return (T) Models.modelDirective().enableMetaCompleted(this);
    }

    default <T extends DataMetaBit> T disableMetaCompleted() {
        return (T) Models.modelDirective().disableMetaCompleted(this);
    }

    @JSONField(serialize = false)
    default boolean isMetaCompleted() {
        return Models.modelDirective().isMetaCompleted(this);
    }

    default <T extends DataMetaBit> T enableMetaDiffing() {
        return (T) Models.modelDirective().enableMetaDiffing(this);
    }

    default <T extends DataMetaBit> T disableMetaDiffing() {
        return (T) Models.modelDirective().disableMetaDiffing(this);
    }

    @JSONField(serialize = false)
    default boolean isMetaDiffing() {
        return Models.modelDirective().isMetaDiffing(this);
    }

    default <T extends DataMetaBit> T enableMetaCrossing() {
        return (T) Models.modelDirective().enableMetaCrossing(this);
    }

    default <T extends DataMetaBit> T disableMetaCrossing() {
        return (T) Models.modelDirective().disableMetaCrossing(this);
    }

    @JSONField(serialize = false)
    default boolean isMetaCrossing() {
        return Models.modelDirective().isMetaCrossing(this);
    }

    default <T extends DataMetaBit> T enableDefaultValue() {
        return (T) Models.modelDirective().enableDefaultValue(this);
    }

    default <T extends DataMetaBit> T disableDefaultValue() {
        return (T) Models.modelDirective().disableDefaultValue(this);
    }

    @JSONField(serialize = false)
    default boolean isDoDefaultValue() {
        return Models.modelDirective().isDoDefaultValue(this);
    }

}
