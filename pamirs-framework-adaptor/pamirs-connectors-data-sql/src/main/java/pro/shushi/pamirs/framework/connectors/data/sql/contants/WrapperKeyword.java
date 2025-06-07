package pro.shushi.pamirs.framework.connectors.data.sql.contants;

import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * wrapper 内部使用枚举
 */
public enum WrapperKeyword implements ISqlSegment {
    /**
     * 只用作于辨识,不用于其他
     */
    APPLY(null),
    LEFT_BRACKET(CharacterConstants.LEFT_BRACKET),
    RIGHT_BRACKET(CharacterConstants.RIGHT_BRACKET);

    private final String keyword;

    WrapperKeyword(final String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSqlSegment() {
        return keyword;
    }
}
