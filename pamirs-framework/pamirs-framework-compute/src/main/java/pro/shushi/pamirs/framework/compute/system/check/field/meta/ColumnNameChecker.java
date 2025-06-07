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

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_CHECK_COLUMN_NAME_ERROR;

/**
 * 列名称检查
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 1:16 下午
 */
@Fun
public class ColumnNameChecker implements Checker<String>, MetaCheckConstants {

    @Function.Advanced(displayName = "校验列名称", type = FunctionTypeEnum.QUERY)
    @Function.fun(checkColumnName)
    @Override
    public Boolean check(String value) {
        if (null == value) {
            return true;
        }
        if (StringUtils.isNotBlank(value)) {
            if (!CheckUtils.onlyLetterAndNumberAndUnderline(value)) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_COLUMN_NAME_ERROR).setData(value));
                return false;
            }
        }
        return true;
    }

}
