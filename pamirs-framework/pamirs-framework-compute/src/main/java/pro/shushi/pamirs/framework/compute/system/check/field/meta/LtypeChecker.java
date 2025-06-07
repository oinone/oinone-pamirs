package pro.shushi.pamirs.framework.compute.system.check.field.meta;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.Checker;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_LTYPE_IS_INVALID_ERROR;

/**
 * Java类型支持检查
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 1:16 下午
 */
@Fun
public class LtypeChecker implements Checker<String>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验代码类型", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkLtype)
    @Override
    public Boolean check(String value) {
        if (null == value) {
            return true;
        }
        if (StringUtils.isNotBlank(value)) {
            boolean result = true;
            try {
                if (!TypeUtils.isValidLtype(value)) {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
            if (!result) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_LTYPE_IS_INVALID_ERROR).setData(value));
                return false;
            }
        }
        return true;
    }

}
