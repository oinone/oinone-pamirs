package pro.shushi.pamirs.framework.connectors.data.configure.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.framework.connectors.data.api.service.DataSourceRouteService;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_DATA_SOURCE_ROUTE_SERVICE_ERROR;

/**
 * String to DataSourceRouteService bean 转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 12:57 上午
 */
public class String2DataSourceRouteServiceConverter implements Converter<String, DataSourceRouteService> {

    @Override
    public DataSourceRouteService convert(@Nullable String s) {
        try {
            return (DataSourceRouteService) Class.forName(s).newInstance();
        } catch (Exception e) {
            throw PamirsException.construct(BASE_DATA_SOURCE_ROUTE_SERVICE_ERROR).errThrow();
        }
    }

}
