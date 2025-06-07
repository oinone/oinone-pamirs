package pro.shushi.pamirs.boot.open.auth;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.auth.AuthApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Set;

/**
 * 权限校验实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order(77)
@SPI.Service
public class DefaultAuthApi implements AuthApi {

    @Override
    public Result<Void> canAccessModule(String module) {
        return new Result<>();
    }

    @Override
    public Result<Void> canAccessHomepage(String module) {
        return new Result<>();
    }

    @Override
    public Result<Void> canAccessMenu(String module, String name) {
        return new Result<>();
    }

    @Override
    public Result<Void> canAccessAction(String model, String name) {
        return new Result<>();
    }

    @Override
    public Result<Void> canAccessAction(String path) {
        return new Result<>();
    }

    @Override
    public Result<Void> canAccessFunction(String namespace, String fun) {
        return new Result<>();
    }

    @Override
    public Result<String> canReadableData(String model) {
        return new Result<>();
    }

    @Override
    public Result<String> canWritableData(String model) {
        return new Result<>();
    }

    @Override
    public Result<String> canDeletableData(String model) {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canReadableFields(String model) {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canWritableFields(String model) {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canAccessModules() {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canAccessHomepages() {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canAccessMenus(String module) {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canAccessActions() {
        return new Result<>();
    }

    @Override
    public Result<Set<String>> canAccessActions(String model) {
        return new Result<>();
    }
}
