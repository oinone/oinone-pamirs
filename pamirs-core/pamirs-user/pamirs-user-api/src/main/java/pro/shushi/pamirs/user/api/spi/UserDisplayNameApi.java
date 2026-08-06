package pro.shushi.pamirs.user.api.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.UserDisplayName;

import java.util.Collections;
import java.util.List;

/**
 * 用户展示名扩展点（通用）。
 * <p>
 * 按用户 Id / Code / 用户对象解析展示名称；业务侧通过
 * {@code Spider.getLoader(UserDisplayNameApi.class).getExtension()} 调用，无需单独 Fun 服务。
 * 平台默认与 {@code UserNameBehavior} 对齐：有编码时为 {@code code-name}，否则仅名称。
 * <p>
 * 扩展方式：
 * <pre>{@code
 * @Order(0)
 * @Component
 * @SPI.Service
 * public class BizUserDisplayNameApi implements UserDisplayNameApi {
 *     public UserDisplayName display(PamirsUser user, String scene) {
 *         // 中文名 / 中文名-工号 / 中文名 + subtitle=(部门)
 *     }
 * }
 * }</pre>
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface UserDisplayNameApi {

    /**
     * 由已加载的用户对象格式化展示名（尽量不查库）。
     */
    UserDisplayName display(PamirsUser user, String scene);

    /**
     * 便捷：只取主展示字符串。
     */
    default String displayName(PamirsUser user, String scene) {
        UserDisplayName display = display(user, scene);
        return display == null ? null : display.getDisplayName();
    }

    /**
     * 按用户 Id 批量解析。
     */
    List<UserDisplayName> resolveByIds(List<Long> userIds, String scene);

    /**
     * 按用户 Code 批量解析。
     */
    default List<UserDisplayName> resolveByCodes(List<String> codes, String scene) {
        return Collections.emptyList();
    }

    /**
     * 已有用户列表时批量格式化。
     */
    default List<UserDisplayName> resolveByUsers(List<PamirsUser> users, String scene) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserDisplayName> result = new java.util.ArrayList<>(users.size());
        for (PamirsUser user : users) {
            UserDisplayName item = display(user, scene);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
}
