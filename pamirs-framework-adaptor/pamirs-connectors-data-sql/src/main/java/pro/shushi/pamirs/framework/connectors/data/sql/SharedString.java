package pro.shushi.pamirs.framework.connectors.data.sql;


import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.io.Serializable;

/**
 * 共享查询字段
 */
@Data
public class SharedString implements Serializable {
    private static final long serialVersionUID = -1536422416594422874L;

    /**
     * 共享的 string 值
     */
    private String stringValue;

    public SharedString() {
        super();
    }

    public SharedString(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * SharedString 里是 ""
     */
    public static SharedString emptyString() {
        return new SharedString(CharacterConstants.SEPARATOR_EMPTY);
    }

}
