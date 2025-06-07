package pro.shushi.pamirs.middleware.schedule.core.function.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import pro.shushi.pamirs.middleware.schedule.common.Result;
import pro.shushi.pamirs.middleware.schedule.core.function.FunctionReturnResultConverter;

/**
 * @author Adamancy Zhang
 * @date 2020-10-23 02:21
 */
public class DefaultReturnResultConverter implements FunctionReturnResultConverter<Result<Void>> {

    public static final DefaultReturnResultConverter INSTANCE = new DefaultReturnResultConverter();

    private DefaultReturnResultConverter() {
        //reject create object
    }

    @Override
    public Result<Void> convert(Object value) {
        if (value == null) {
            return new Result<>();
        }
        return JSON.parseObject(JSON.toJSONString(value), new TypeReference<Result<Void>>() {
        }.getType());
    }
}
