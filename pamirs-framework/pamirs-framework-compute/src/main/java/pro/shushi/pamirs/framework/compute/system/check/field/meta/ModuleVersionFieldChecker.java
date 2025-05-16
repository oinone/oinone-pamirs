package pro.shushi.pamirs.framework.compute.system.check.field.meta;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.Checker;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_MODULE_VERSION_FORMAT_ERROR;
import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_MODULE_VERSION_NOT_DIGIT_ERROR;

/**
 * 模块版本格式校验
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:55 上午
 */
@Fun
public class ModuleVersionFieldChecker implements Checker<String>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验模块版本号", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkModuleVersion)
    @Override
    public Boolean check(String value) {
        if (null == value) {
            return true;
        }
        if (StringUtils.isNotBlank(value)) {
            if (value.split(CharacterConstants.SEPARATOR_ESCAPE_DOT).length != 3) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_MODULE_VERSION_FORMAT_ERROR).setData(value));
                return false;
            }
            if (!NumberUtils.isDigits(value.replace(CharacterConstants.SEPARATOR_DOT, CharacterConstants.SEPARATOR_EMPTY))) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_MODULE_VERSION_NOT_DIGIT_ERROR).setData(value));
                return false;
            }
        }
        return true;
    }

}
