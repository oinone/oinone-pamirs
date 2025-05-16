package pro.shushi.pamirs.framework.compute.system.check.field.meta;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.compute.system.check.util.CheckUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.Checker;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_CHECK_FIELD_NAME_ERROR;

/**
 * 字段技术名称检查(允许#标识常量)
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 1:16 下午
 */
@Fun
public class FieldNameConfigChecker implements Checker<String>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验字段api名称(允许包含'#')", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkFieldConfig)
    @Override
    public Boolean check(String value) {
        if (null == value) {
            return true;
        }
        if (StringUtils.isNotBlank(value)) {
            if (FieldUtils.isConstantRelationFieldValue(value)) {
//                value = value.substring(1, value.length() - 2);
                return true;
            }
            if (!CheckUtils.onlyLowercaseLetterAndNumber(value)) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_NAME_ERROR).setData(value));
                return false;
            }
        }
        return true;
    }

}
