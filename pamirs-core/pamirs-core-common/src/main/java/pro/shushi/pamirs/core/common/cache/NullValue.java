package pro.shushi.pamirs.core.common.cache;

import java.io.Serializable;

/**
 * @author Adamancy Zhang
 * @date 2021-01-12 16:12
 */
public class NullValue implements Serializable {

    private static final long serialVersionUID = 8262659335000105399L;

    private static final NullValue INSTANCE = new NullValue();

    private NullValue() {
        //reject create object
    }

    public static NullValue getInstance() {
        return INSTANCE;
    }
}
