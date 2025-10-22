package pro.shushi.pamirs.draft.constant;

import pro.shushi.pamirs.meta.constant.MetaDefaultConstants;

/**
 * 草稿常量
 *
 * @author Adamancy Zhang at 15:28 on 2025-10-20
 */
public interface DraftConstants {

    String DB_STORAGE = "db";

    String REDIS_STORAGE = "redis";

    String REDIS_TEMPLATE_BEAN_NAME = "draftRedisTemplate";

    String CACHE_KEY_PREFIX = "pamirs:draft:";

    String ANONYMOUS_USER = "anonymous";

    interface SaveDraft {
        String displayName = "保存为草稿";
        String name = "internalSaveDraft";
        String fun = "$$internal_SaveDraft";
        int priority = MetaDefaultConstants.PRIORITY_VALUE_INT + 1;
    }
}
