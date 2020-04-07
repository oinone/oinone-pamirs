package pro.shushi.pamirs.meta.api.core.session;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.session.RequestContext;

import java.io.Serializable;

/**
 * session构造器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface SessionConstructor extends CommonApi {

    /**
     * 获取用户id
     *
     * @return
     */
    <T extends Serializable> T getUserId();

    /**
     * 设置用户id
     *
     * @param userId
     */
    <T extends Serializable> void setUserId(T userId);

    /**
     * 获取模块
     *
     * @return
     */
    String getAppId();

    /**
     * 设置模块
     *
     * @param appId
     */
    void setAppId(String appId);

    /**
     * 获取语言
     *
     * @return
     */
    String getLang();

    /**
     * 设置语言
     *
     * @param lang
     */
    void setLang(String lang);

    /**
     * 获取租户
     *
     * @return
     */
    String getTenant();

    /**
     * 设置租户
     *
     * @param tenant
     */
    void setTenant(String tenant);

    /**
     * 获取环境
     *
     * @return
     */
    boolean isPreview();

    /**
     * 设置环境
     *
     * @param preview
     */
    void setPreview(boolean preview);

    /**
     * 获取请求上下文
     *
     * @return
     */
    RequestContext getContext();

    /**
     * 设置请求上下文
     *
     * @param context
     */
    void setContext(RequestContext context);

    void clear();

}
