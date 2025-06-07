package pro.shushi.pamirs.boot.web.utils;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * 元数据加载工具类
 * <p>
 * 2022/11/30 5:42 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class LoaderUtils {

    public static <T> QueryWrapper<T> authQuery(QueryWrapper<T> wrapper) {
        Result<String> result = AuthApi.get().canReadableData(wrapper.getModel());
        // 管理员标识的判断，需要放在canReadAccessData下面。在canReadAccessData才会有角色的计算
        if (PamirsSession.getAdminTag() != null && PamirsSession.getAdminTag()) {
            return wrapper;
        } else {
            String data = result.getData();
            if (!StringUtils.isBlank(data)) {
                wrapper.apply(data);
            }
            /** 权限逻辑先恢复-不配置等于拥有
             else {
             wrapper.apply("1!=1");
             }**/
        }
        return wrapper;
    }

    public static <T> LambdaQueryWrapper<T> authQuery(LambdaQueryWrapper<T> wrapper) {
        Result<String> result = AuthApi.get().canReadableData(wrapper.getModel());
        // 管理员标识的判断，需要放在canReadAccessData下面。在canReadAccessData才会有角色的计算
        if (PamirsSession.getAdminTag() != null && PamirsSession.getAdminTag()) {
            return wrapper;
        } else {
            String data = result.getData();
            if (!StringUtils.isBlank(data)) {
                wrapper.apply(data);
            }
            /** 权限逻辑先恢复-不配置等于拥有
             else {
             wrapper.apply("1!=1");
             }**/
        }
        return wrapper;
    }


    /**
     * 角色和权限组都可以置为无效，存在根据用户查询不到权限的情况
     *
     * @param wrapper
     * @param <T>
     * @return
     */
    public static <T> LambdaQueryWrapper<T> authQueryAllowNull(LambdaQueryWrapper<T> wrapper) {
        Result<String> result = AuthApi.get().canReadableData(wrapper.getModel());
        // 管理员标识的判断，需要放在canReadAccessData下面。在canReadAccessData才会有角色的计算
        if (PamirsSession.getAdminTag() != null && PamirsSession.getAdminTag()) {
            return wrapper;
        } else {
            String data = result.getData();
            if (!StringUtils.isBlank(data)) {
                wrapper.apply(data);
            }
        }
        return wrapper;
    }


}
