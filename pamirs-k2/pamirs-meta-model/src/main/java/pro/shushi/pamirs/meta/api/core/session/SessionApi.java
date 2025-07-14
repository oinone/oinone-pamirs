package pro.shushi.pamirs.meta.api.core.session;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * session租户api
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface SessionApi extends CommonApi {

    /**
     * 获取用户id
     *
     * @return 用户
     */
    <T extends Serializable> T getUserId();

    /**
     * 设置用户id
     *
     * @param userId 用户
     */
    <T extends Serializable> void setUserId(T userId);

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    String getUserName();

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    void setUserName(String userName);

    /**
     * 获取用户CODE
     *
     * @return 用户CODE
     */
    String getUserCode();

    /**
     * 设置用户CODE
     *
     * @param userCode 用户CODE
     */
    void setUserCode(String userCode);

    /**
     * 获取用户标识是否为超级管理员
     *
     * @return 是否为超级管理员
     */
    Boolean isAdmin();

    /**
     * 设置用户标识为超级管理员
     *
     * @param isAdmin 是否为超级管理员
     */
    void setIsAdmin(Boolean isAdmin);

    /**
     * 获取用户标识是否为匿名用户
     *
     * @return 是否为匿名用户
     */
    Boolean isAnonymous();

    /**
     * 设置用户标识为匿名用户
     *
     * @param isAnonymous 是否为匿名用户
     */
    void setIsAnonymous(Boolean isAnonymous);

    /**
     * 获取产品编码
     *
     * @return 产品编码
     */
    String getProduct();

    /**
     * 设置产品编码
     *
     * @param product 产品编码
     */
    void setProduct(String product);

    /**
     * 获取http sessionId
     *
     * @return http sessionId
     */
    String getSessionId();

    /**
     * 设置http sessionId
     *
     * @param sessionId http sessionId
     */
    void setSessionId(String sessionId);

    /**
     * 获取模块编码
     *
     * @return 模块编码
     */
    String getAppId();

    /**
     * 设置模块编码
     *
     * @param appId 模块编码
     */
    void setAppId(String appId);

    /**
     * 获取模块名称
     *
     * @return 模块名称
     */
    String getAppName();

    /**
     * 设置模块名称
     *
     * @param appName 模块名称
     */
    void setAppName(String appName);

    /**
     * 获取服务模块编码
     *
     * @return 模块编码
     */
    String getServApp();

    /**
     * 设置服务模块编码
     *
     * @param appName 模块编码
     */
    void setServApp(String appName);

    /**
     * 获取请求发起模块
     *
     * @return 模块编码
     */
    String getRequestFromModule();

    /**
     * 设置请求发起模块
     *
     * @param requestFromModule 模块编码
     */
    void setRequestFromModule(String requestFromModule);

    /**
     * 获取语言
     *
     * @return 语言
     */
    String getLang();

    /**
     * 设置语言
     *
     * @param lang 语言
     */
    void setLang(String lang);

    /**
     * 获取国家
     *
     * @return 国家
     */
    String getCountry();

    /**
     * 设置国家
     *
     * @param country 国家
     */
    void setCountry(String country);

    /**
     * 是否预览环境
     *
     * @return 是否预览环境
     */
    Boolean isPreview();

    /**
     * 获取环境
     *
     * @return 环境
     */
    String getEnv();

    /**
     * 设置环境
     *
     * @param env 环境
     */
    void setEnv(String env);

    /**
     * 获取子线程可见扩展数据
     *
     * @return 扩展数据
     */
    Map<String, String> getTransmittableExtend();

    /**
     * 设置子线程可见扩展数据
     *
     * @param extend 扩展数据
     */
    void setTransmittableExtend(Map<String, String> extend);

    /**
     * 获取数据源key
     *
     * @return 数据源key
     */
    Object getDsKey();

    /**
     * 清除数据源key
     *
     * @return 被清除数据源key
     */
    Object popDsKey();

    /**
     * 设置数据源key
     *
     * @param dsKey 数据源key
     */
    void pushDsKey(Object dsKey);

    /**
     * 获取属性映射模型
     *
     * @return 属性映射模型
     */
    String getAsProperty();

    /**
     * 清除属性映射模型
     *
     * @return 被清除属性映射模型
     */
    String popAsProperty();

    /**
     * 设置属性映射模型
     *
     * @param model 属性映射模型
     */
    void pushAsProperty(String model);

    /**
     * 获取批量操作
     *
     * @return 批量操作数量
     */
    Integer getBatchSize();

    /**
     * 清除批量操作
     *
     * @return 被清除批量操作数量
     */
    Integer popBatchSize();

    /**
     * 设置批量操作
     *
     * @param batchSize 批量操作数量
     */
    void pushBatchSize(Integer batchSize);

    /**
     * 获取批量操作类型
     *
     * @return 批量操作类型
     */
    BatchCommitTypeEnum getBatchOperation();

    /**
     * 设置批量操作类型
     *
     * @param operation 批量操作类型
     */
    void setBatchOperation(BatchCommitTypeEnum operation);

    /**
     * 获取信息中心
     *
     * @return 信息中心
     */
    MessageHub getMessageHub();

    /**
     * 设置信息中心
     *
     * @param messageHub 信息中心
     */
    void setMessageHub(MessageHub messageHub);

    /**
     * 获取指令系统
     *
     * @return 指令系统
     */
    SessionMetaBit directive();

    /**
     * 获取请求变量
     *
     * @return RequestContext
     */
    PamirsRequestVariables getRequestVariables();

    /**
     * 设置请求变量
     *
     * @param requestVariables 请求变量
     */
    void setRequestVariables(PamirsRequestVariables requestVariables);

    /**
     * 是否是静态配置
     */
    boolean isStaticConfig();

    /**
     * 设置当前为静态配置调用
     */
    void setStaticConfig(boolean staticConfig);

    /**
     * 获取子线程不可见扩展数据
     *
     * @return 扩展数据
     */
    Map<String, String> getKernelExtend();

    /**
     * 设置子线程不可见扩展数据
     *
     * @param extend 扩展数据
     */
    void setKernelExtend(Map<String, String> extend);

    /**
     * 获取session
     *
     * @return 获取session map
     */
    default Map<String, String> fetchSessionMap() {
        return Sessions.fetchSessionMap();
    }

    /**
     * 设置session map到 session
     *
     * @param sessionMap session map
     */
    default void fillSessionFromMap(Map<String, String> sessionMap) {
        Sessions.fillSessionFromMap(sessionMap);
    }

    @Deprecated
    void setAdminTag(Boolean adminTag);

    @Deprecated
    Boolean getAdminTag();
}
