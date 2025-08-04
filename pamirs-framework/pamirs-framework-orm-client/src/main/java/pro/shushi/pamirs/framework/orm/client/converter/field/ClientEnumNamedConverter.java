package pro.shushi.pamirs.framework.orm.client.converter.field;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.entity.handler.EnumNamedHandler;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import jakarta.annotation.Resource;

/**
 * 字段枚举转换服务
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientEnumNamedConverter {

    @Resource
    private EnumNamedHandler enumNamedHandler;

    public Object in(ModelFieldConfig fieldConfig, Object fieldValue) {
        return enumNamedHandler.toEnum(fieldConfig, fieldValue, 0);
    }

    public Object out(ModelFieldConfig fieldConfig, Object fieldValue) {
        return enumNamedHandler.stringify(fieldConfig, fieldValue, 0);
    }

}
