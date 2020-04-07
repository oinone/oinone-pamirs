package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.configure.ModelModelFetcher;
import pro.shushi.pamirs.meta.api.core.session.SessionConstructor;

import java.io.Serializable;

/**
 * pamirs session
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:45 下午
 */
public class PamirsSession {

    public static String getTenant(){
        return MetaApiFactory.getApi(SessionConstructor.class).getTenant();
    }

    public static boolean isPreview(){
        return MetaApiFactory.getApi(SessionConstructor.class).isPreview();
    }

    public static String getAppId(){
        return MetaApiFactory.getApi(SessionConstructor.class).getAppId();
    }

    public static <T extends Serializable> T getUserId(){
        return MetaApiFactory.getApi(SessionConstructor.class).getUserId();
    }

    public static String getLang(){
        return MetaApiFactory.getApi(SessionConstructor.class).getLang();
    }

    public static void setTenant(String tenant){
        MetaApiFactory.getApi(SessionConstructor.class).setTenant(tenant);
    }

    public static void setPreview(boolean preview){
        MetaApiFactory.getApi(SessionConstructor.class).setPreview(preview);
    }

    public static void setAppId(String appId){
        MetaApiFactory.getApi(SessionConstructor.class).setAppId(appId);
    }

    public static <T extends Serializable> void setUserId(T userId){
        MetaApiFactory.getApi(SessionConstructor.class).setUserId(userId);
    }

    public static void setLang(String lang){
        MetaApiFactory.getApi(SessionConstructor.class).setLang(lang);
    }

    public static RequestContext getContext(){
        return MetaApiFactory.getApi(SessionConstructor.class).getContext();
    }

    public static void setContext(RequestContext context){
        MetaApiFactory.getApi(SessionConstructor.class).setContext(context);
    }

    public static void clear(){
        MetaApiFactory.getApi(SessionConstructor.class).clear();
    }

}
