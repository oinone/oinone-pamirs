package pro.shushi.pamirs.auth.api.runtime.spi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.debug.AuthVerificationDebugTrace;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.helper.AuthorizedValueHelper;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.spi.DataPermissionApi;
import pro.shushi.pamirs.auth.api.runtime.utils.AuthFilterHelper;
import pro.shushi.pamirs.auth.api.spi.service.DefaultAuthFilterService;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 默认数据权限实现
 *
 * @author Adamancy Zhang at 11:46 on 2024-01-10
 */
@Order
@Component
@SPI.Service
public class DefaultDataPermission implements DataPermissionApi {

    @Autowired
    private DefaultAuthFilterService defaultAuthFilterService;

    @Override
    public AuthResult<Boolean> isAccessModel(String model, List<FunctionTypeEnum> functionTypes) {
        return AuthFilterHelper.executeBooleanFilter((api) -> api.isAccessModel(model, functionTypes),
                () -> AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> AuthApiHolder.getDataPermissionCacheApi().fetchModelPermission(roleIds, model))
                        .transfer((authorizedValue) -> {
                            for (FunctionTypeEnum functionType : functionTypes) {
                                if ((authorizedValue & AuthorizedValueHelper.getModelAuthorizedValueEnum(functionType).value()) == 0) {
                                    return Boolean.FALSE;
                                }
                            }
                            return Boolean.TRUE;
                        }));
    }

    @Override
    public AuthResult<Map<String, Long>> fetchFieldPermissions(String model) {
        return AuthFilterHelper.executeFilter((api) -> api.fetchFieldPermissions(model),
                () -> AuthApiHolder.getFetchPermissionApi().fetch((accessInfo, roleIds) -> AuthApiHolder.getDataPermissionCacheApi().fetchFieldPermissions(roleIds, model)));
    }

    @Override
    public AuthResult<String> fetchModelFilterForRead(String model) {
        return debug(() -> AuthFilterHelper.executeFilter((api) -> api.fetchModelFilterForRead(model), () -> defaultAuthFilterService.fetchModelFilterForRead(model)),
                result -> AuthVerificationDebugTrace.debugReadableData(result.getData(), model));
    }

    @Override
    public AuthResult<String> fetchModelFilterForWrite(String model) {
        return debug(() -> AuthFilterHelper.executeFilter((api) -> api.fetchModelFilterForWrite(model), () -> defaultAuthFilterService.fetchModelFilterForWrite(model)),
                result -> AuthVerificationDebugTrace.debugWritableData(result.getData(), model));
    }

    @Override
    public AuthResult<String> fetchModelFilterForDelete(String model) {
        return debug(() -> AuthFilterHelper.executeFilter((api) -> api.fetchModelFilterForDelete(model), () -> defaultAuthFilterService.fetchModelFilterForDelete(model)),
                result -> AuthVerificationDebugTrace.debugDeletableData(result.getData(), model));
    }

    private <R> AuthResult<R> debug(Supplier<AuthResult<R>> supplier, Consumer<AuthResult<R>> debugConsumer) {
        AuthResult<R> result = supplier.get();
        debugConsumer.accept(result);
        return result;
    }
}
