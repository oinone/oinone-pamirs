package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 表达式中session上下文
 * 2021/3/5 11:05 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class SessionContext {

    /**
     * 模块
     */
    private String module;

    /**
     * 请求发起模块
     */
    private String requestFromModule;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 语言
     */
    private String lang;

    /**
     * 国家
     */
    private String country;

    /**
     * 环境
     */
    private String env;

    /**
     * 扩展信息
     */
    private Map<String, Object> extend = new HashMap<>();

}
