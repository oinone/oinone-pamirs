package pro.shushi.pamirs.framework.compute.system.check.field.meta;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.compute.system.check.util.CheckUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.Checker;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_CHECK_MODULE_MODULE_ERROR;
import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_CHECK_MODULE_NAME_START_WITH_EN;
import static pro.shushi.pamirs.meta.common.util.PStringUtils.startWithNumber;

/**
 * 模块编码检查
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 1:16 下午
 */
@Fun
public class ModuleModuleChecker implements Checker<String>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验模块编码", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkModuleModule)
    @Override
    public Boolean check(String value) {
        if (null == value) {
            return true;
        }
        if (StringUtils.isNotBlank(value)) {
            if (startWithNumber(value) || value.startsWith(CharacterConstants.SEPARATOR_UNDERLINE)) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_MODULE_NAME_START_WITH_EN).setData(value));
                return false;
            }

            if (!CheckUtils.onlyLetterAndNumberAndUnderline(value)) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_MODULE_MODULE_ERROR).setData(value));
                return false;
            }
        }
        return true;
    }

}
