package pro.shushi.pamirs.framework.orm.json.handler;

import pro.shushi.pamirs.framework.orm.converter.entity.handler.EnumHandler;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.util.Map;

/**
 * 枚举处理
 * <p>
 * 2021/9/24 3:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataEnumHandler {

    private final static HoldKeeper<EnumHandler> enumHandlerHoldKeeper = new HoldKeeper<>();

    public static EnumHandler getEnumConverter() {
        return enumHandlerHoldKeeper.supply(() -> BeanDefinitionUtils.getBean(EnumHandler.class));
    }

    public static void toEnum(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        getEnumConverter().toEnum(fieldConfig, origin, features);
    }

    public static void stringify(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        getEnumConverter().stringify(fieldConfig, origin, features);
    }

}
