package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 批量操作
 * <p>
 * 2021/3/2 3:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class BatchOperation {

    private int read = 500;

    private int write = 2000;

}
