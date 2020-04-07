package pro.shushi.pamirs.meta.api.dto.config;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 事务配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 *
 */
@Data
public class TxConfig {

    private boolean active = true;

    private String namespace;

    private String fun;

    private String ds;

    private Integer isolation = -1;

    private Integer propagation = 0;

    private Integer timeout = -1;

    private Boolean readOnly = false;

    private Class<? extends Throwable>[] rollbackFor = new Class[]{};

    private String[] rollbackForClassName = new String[]{};

    private Class<? extends Throwable>[] noRollbackFor = new Class[]{};

    private String[] noRollbackForClassName = new String[]{};

    public Map<String, Object> map(){
        Map<String, Object> txMap = new HashMap<>();
        txMap.put("active", active);
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
