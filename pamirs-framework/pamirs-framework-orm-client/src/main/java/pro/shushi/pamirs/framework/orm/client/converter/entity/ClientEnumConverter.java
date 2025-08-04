package pro.shushi.pamirs.framework.orm.client.converter.entity;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.entity.handler.EnumHandler;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 前端枚举转换服务
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientEnumConverter {

    @Resource
    private EnumHandler enumHandler;

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        enumHandler.toEnum(fieldConfig, origin, 0);
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        enumHandler.stringify(fieldConfig, origin, 0);
    }

}
