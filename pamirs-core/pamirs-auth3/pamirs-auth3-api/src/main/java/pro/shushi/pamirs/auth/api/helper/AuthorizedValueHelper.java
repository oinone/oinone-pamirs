package pro.shushi.pamirs.auth.api.helper;

import pro.shushi.pamirs.auth.api.enumeration.authorized.ModelAuthorizedValueEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

/**
 * 权限值帮助类
 *
 * @author Adamancy Zhang at 12:00 on 2024-01-10
 */
public class AuthorizedValueHelper {

    private AuthorizedValueHelper() {
        // reject create object
    }

    public static ModelAuthorizedValueEnum getModelAuthorizedValueEnum(FunctionTypeEnum functionType) {
        switch (functionType) {
            case CREATE:
                return ModelAuthorizedValueEnum.CREATE;
            case DELETE:
                return ModelAuthorizedValueEnum.DELETE;
            case UPDATE:
                return ModelAuthorizedValueEnum.UPDATE;
            case QUERY:
                return ModelAuthorizedValueEnum.QUERY;
            default:
                throw new IllegalArgumentException("Invalid function type.");
        }
    }

    public static Long getModelAuthorizedValue(List<FunctionTypeEnum> functionTypes) {
        Long authorizedValue = 0L;
        for (FunctionTypeEnum functionType : functionTypes) {
            authorizedValue |= getModelAuthorizedValueEnum(functionType).value();
        }
        return authorizedValue;
    }
}
