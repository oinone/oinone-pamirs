package pro.shushi.pamirs.user.core.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.api.model.tmodel.UserDisplayName;
import pro.shushi.pamirs.user.api.spi.UserDisplayNameApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 平台默认用户展示名：与 {@code UserNameBehavior} 对齐——有编码时为 {@code code-name}，否则仅名称。
 * <p>{@code @Order} 无参 = 最低优先级，业务实现声明更高优先级即可覆盖（如中文名-工号、中文名(部门)）。
 */
@Order
@Slf4j
@Component
@SPI.Service
public class DefaultUserDisplayNameApi implements UserDisplayNameApi {

    @Override
    public UserDisplayName display(PamirsUser user, String scene) {
        if (user == null) {
            return null;
        }
        String name = firstNonBlank(user.getRealname(), user.getName(), user.getNickname(), user.getLogin());
        if (StringUtils.isBlank(name)) {
            return null;
        }
        String code = StringUtils.trimToNull(user.getCode());
        UserDisplayName result = new UserDisplayName();
        result.setUserId(user.getId());
        result.setCode(code);
        // 默认：code-name（与 UserNameBehavior 一致）；无 code 则仅 name
        result.setDisplayName(code == null ? name : code + "-" + name);
        return result;
    }

    @Override
    public List<UserDisplayName> resolveByIds(List<Long> userIds, String scene) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (distinctIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<PamirsUser> users = Models.origin().queryListByWrapper(Pops.<PamirsUser>lambdaQuery()
                    .from(PamirsUser.MODEL_MODEL)
                    .select(PamirsUser::getId, PamirsUser::getCode, PamirsUser::getName,
                            PamirsUser::getRealname, PamirsUser::getNickname, PamirsUser::getLogin)
                    .in(PamirsUser::getId, distinctIds));
            return resolveByUsers(users, scene);
        } catch (Throwable e) {
            log.warn("[用户展示名] 按Id查询失败, userIds={}", distinctIds, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<UserDisplayName> resolveByCodes(List<String> codes, String scene) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }
        List<String> distinctCodes = codes.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
        if (distinctCodes.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<PamirsUser> users = Models.origin().queryListByWrapper(Pops.<PamirsUser>lambdaQuery()
                    .from(PamirsUser.MODEL_MODEL)
                    .select(PamirsUser::getId, PamirsUser::getCode, PamirsUser::getName,
                            PamirsUser::getRealname, PamirsUser::getNickname, PamirsUser::getLogin)
                    .in(PamirsUser::getCode, distinctCodes));
            return resolveByUsers(users, scene);
        } catch (Throwable e) {
            log.warn("[用户展示名] 按Code查询失败, codes={}", distinctCodes, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<UserDisplayName> resolveByUsers(List<PamirsUser> users, String scene) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<UserDisplayName> result = new ArrayList<>(users.size());
        for (PamirsUser user : users) {
            UserDisplayName item = display(user, scene);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }
}
