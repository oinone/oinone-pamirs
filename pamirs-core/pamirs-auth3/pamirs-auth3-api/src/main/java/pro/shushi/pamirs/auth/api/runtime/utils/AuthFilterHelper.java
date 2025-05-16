package pro.shushi.pamirs.auth.api.runtime.utils;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.spi.AuthFilterService;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 权限过滤帮助类
 *
 * @author Adamancy Zhang at 23:57 on 2024-04-22
 */
public class AuthFilterHelper {

    private static final HoldKeeper<List<AuthFilterService>> FILTER_SERVICES = new HoldKeeper<>();

    private static List<AuthFilterService> get() {
        return FILTER_SERVICES.supply(() -> Spider.getLoader(AuthFilterService.class).getOrderedExtensions());
    }

    private AuthFilterHelper() {
        // reject create object
    }

    public static AuthResult<Boolean> executeBooleanFilter(Function<AuthFilterService, Boolean> function) {
        Boolean isAccess = executeUnwrap(function);
        if (isAccess == null) {
            return null;
        }
        if (isAccess) {
            return AuthResult.success(Boolean.TRUE);
        }
        return AuthResult.success(Boolean.FALSE);
    }

    public static AuthResult<Boolean> executeBooleanFilter(Function<AuthFilterService, Boolean> function, Supplier<AuthResult<Boolean>> defaultSupplier) {
        Boolean isAccess = executeUnwrap(function);
        if (isAccess == null) {
            return defaultSupplier.get();
        }
        if (isAccess) {
            return AuthResult.success(Boolean.TRUE);
        }
        return AuthResult.success(Boolean.FALSE);
    }

    public static <R> R executeFilter(Function<AuthFilterService, R> function, Supplier<R> defaultSupplier) {
        R result = executeUnwrap(function);
        if (result == null) {
            return defaultSupplier.get();
        }
        return result;
    }

    private static <R> R executeUnwrap(Function<AuthFilterService, R> function) {
        List<AuthFilterService> services = AuthFilterHelper.get();
        for (AuthFilterService service : services) {
            R isAccess = function.apply(service);
            if (isAccess != null) {
                return isAccess;
            }
        }
        return null;
    }
}
