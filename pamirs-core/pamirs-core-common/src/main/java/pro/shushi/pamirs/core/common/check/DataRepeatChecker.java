package pro.shushi.pamirs.core.common.check;

import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.lambda.Getter;

/**
 * @author zbh
 * @date 2021/9/184:16 下午
 */
@Deprecated
public class DataRepeatChecker {

    public static <T extends IdModel> Boolean isDataRepeat(T data, Getter<T, ?> getter) {
        Object value = getter.apply(data);
        if (value == null) {
            return Boolean.FALSE;
        }
        LambdaQueryWrapper<T> queryWrapper = Pops.lambdaQuery();
        queryWrapper.from(Models.api().getModel(data));
        queryWrapper.eq(getter, value);

        Long excludeId = data.getId();
        if (excludeId != null) {
            queryWrapper.ne(IdModel::getId, excludeId);
        }
        Long count = Models.origin().count(queryWrapper);
        return count != 0;
    }

}
