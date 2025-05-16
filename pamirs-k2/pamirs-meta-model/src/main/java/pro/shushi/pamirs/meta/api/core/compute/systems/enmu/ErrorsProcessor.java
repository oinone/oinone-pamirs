package pro.shushi.pamirs.meta.api.core.compute.systems.enmu;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ErrorDefinition;

import java.util.Map;

/**
 * 错误枚举处理器
 * 2021/3/6 11:21 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
@Order
@SPI
public interface ErrorsProcessor<T> extends CommonApi {

    T fetchErrorsFromEnum(String module, Class enumClass);

    T fillErrorsFromEnum(T errorsDefinition, String module, Class enumClass);

    Map<String, ErrorDefinition> fetchErrorDefinitionMap(Map<String, ErrorDefinition> errorDefinitionMap,
                                                         String module, Class errorEnumClass);

}
