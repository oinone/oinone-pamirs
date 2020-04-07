package pro.shushi.pamirs.meta.api.session;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * pamirs线程变量
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:41 下午
 */
public class PamirsThreadLocal<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -7154481442796541819L;

    private String tenant;

    private Boolean preview;

    private String appId;

    private T userId;

    private String lang;

    private RequestContext context = new RequestContext();

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        if(StringUtils.isNotBlank(this.tenant)){
            return;
        }
        this.tenant = tenant;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        if(null != this.preview){
            return;
        }
        this.preview = preview;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public T getUserId() {
        return userId;
    }

    public void setUserId(T userId) {
        if(null != this.userId){
            return;
        }
        this.userId = userId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void clearContext(){
        context = new RequestContext();
    }

    public RequestContext getContext(){
        return context;
    }

    public void setContext(RequestContext context){
        this.context = context;
    }

}
