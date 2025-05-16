package pro.shushi.pamirs.framework.connectors.data.configure.converter;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_ISQL_INJECTOR_ERROR;

/**
 * String to SqlInjector bean 转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 12:57 上午
 */
public class String2SqlInjectorConverter implements Converter<String, ISqlInjector> {

    @Override
    public ISqlInjector convert(@Nullable String s) {
        try {
            return (ISqlInjector) Class.forName(s).newInstance();
        } catch (Exception e) {
            throw PamirsException.construct(BASE_ISQL_INJECTOR_ERROR, e).errThrow();
        }
    }

}
