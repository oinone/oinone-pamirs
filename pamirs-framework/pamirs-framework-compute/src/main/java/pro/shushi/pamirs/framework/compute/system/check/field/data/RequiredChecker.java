package pro.shushi.pamirs.framework.compute.system.check.field.data;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.Checker;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_CHECK_REQUIRED_ERROR;
import static pro.shushi.pamirs.meta.constant.DataCheckConstants.checkRequired;

/**
 * 字段必填检查
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 1:16 下午
 */
@Fun
public class RequiredChecker implements Checker<Object>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验必填", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkRequired)
    @Override
    public Boolean check(Object value) {
        if (null == value) {
            PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_REQUIRED_ERROR));
            return false;
        }
        return true;
    }

}
