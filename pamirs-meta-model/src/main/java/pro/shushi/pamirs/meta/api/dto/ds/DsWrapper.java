package pro.shushi.pamirs.meta.api.dto.ds;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 2020/7/1 12:22 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class DsWrapper {

    private Object ds;

    private long nanoTime;

    public static DsWrapper wrap(Object ds) {
        return new DsWrapper().setDs(ds).setNanoTime(System.nanoTime());
    }

}
