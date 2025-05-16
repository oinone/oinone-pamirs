package pro.shushi.pamirs.meta.api.dto.protocol;

/**
 * 请求参数上下文变量常量
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/17 1:31 上午
 */
public interface PamirsRequestInfoConstants {

    /**
     * 上下文中的key
     */
    String REQUEST = "oio";

    /**
     * 环境
     */
    String REQUEST_ENV = "env";

    /**
     * 当前产品
     */
    String REQUEST_PRODUCT = "product";

    /**
     * 当前应用
     */
    String REQUEST_APP = "app";

    /**
     * 请求模型
     */
    String REQUEST_MODEL = "model";

    /**
     * 请求编码
     */
    String REQUEST_FUN = "fun";

    /**
     * 请求校验策略
     */
    String REQUEST_STRATEGY_CHECK_STRATEGY = "checkStrategy";

    /**
     * 消息级别
     */
    String REQUEST_STRATEGY_MSG_LEVEL = "msgLevel";

    /**
     * 请求仅校验不返回数据
     */
    String REQUEST_STRATEGY_ONLY_VALIDATE = "onlyValidate";

}
