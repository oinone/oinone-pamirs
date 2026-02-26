package pro.shushi.pamirs.auth.api.runtime.spi.defaults;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.runtime.spi.DataPermissionApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 默认数据权限实现
 *
 * @author Adamancy Zhang at 22:31 on 2026-02-25
 */
@Order
@Component
@SPI.Service
public class DefaultDataPermissionApi implements DataPermissionApi {

    @Override
    public AuthResult<Boolean> isAccessModel(String model, List<FunctionTypeEnum> functionType) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<Map<String, Long>> fetchFieldPermissions(String model) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<String> fetchModelFilterForRead(String model) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<String> fetchModelFilterForWrite(String model) {
        return AuthResult.success();
    }

    @Override
    public AuthResult<String> fetchModelFilterForDelete(String model) {
        return AuthResult.success();
    }
}
