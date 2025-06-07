package pro.shushi.pamirs.framework.orm;

import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

/**
 * 函数API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public class AbstractReadWriteApi {

    protected String getModel(Object modelObject) {
        return Models.api().getModel(modelObject);
    }

    @Deprecated
    protected DataConverter check(DataConverter converter, String model) {
        if (null == converter) {
            throw PamirsException.construct(OrmExpEnumerate.BASE_MODULE_NOT_BOOT_ERROR)
                    .appendMsg("model:" + model).errThrow();
        }
        return converter;
    }

}
