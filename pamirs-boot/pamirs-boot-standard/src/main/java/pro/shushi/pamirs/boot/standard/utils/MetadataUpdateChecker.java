package pro.shushi.pamirs.boot.standard.utils;

import pro.shushi.pamirs.meta.api.session.cache.holder.SessionFillOwnSignApiHolder;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;

/**
 * @author WuXin at 17:00 on 2024/11/25
 */
public class MetadataUpdateChecker {

    public static boolean shouldUpdateMetadata(String model) {
        return SequenceConfig.MODEL_MODEL.equals(model) && SessionFillOwnSignApiHolder.get().handleOwnSign();
    }

}
