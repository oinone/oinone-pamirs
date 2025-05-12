package pro.shushi.pamirs.meta.api.dto.config;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 事务配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 */
@Data
public class TxConfig {

    private boolean enable = true;

    private boolean enableXa = false;

    private String transactionManager;

    private String namespace;

    private String fun;

    private Integer isolation = -1;

    private Integer propagation = 0;

    private Integer timeout = -1;

    private Boolean readOnly = false;

    @SuppressWarnings("unchecked")
    private Class<? extends Throwable>[] rollbackFor = new Class[]{};

    private String[] rollbackForClassName = new String[]{};

    @SuppressWarnings("unchecked")
    private Class<? extends Throwable>[] noRollbackFor = new Class[]{};

    private String[] noRollbackForClassName = new String[]{};

    private String[] rollbackForExpCode;

    private String[] noRollbackForExpCode;

    public Map<String, Object> map() {
        Map<String, Object> txMap = new HashMap<>();
        txMap.put("active", enable);
        txMap.put("isolation", isolation);
        txMap.put("propagation", propagation);
        txMap.put("timeout", timeout);
        txMap.put("readOnly", readOnly);
        txMap.put("rollbackFor", rollbackFor);
        txMap.put("rollbackForClassName", rollbackForClassName);
        txMap.put("noRollbackFor", noRollbackFor);
        txMap.put("noRollbackForClassName", noRollbackForClassName);
        return txMap;
    }

}
