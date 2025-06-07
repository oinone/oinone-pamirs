package pro.shushi.pamirs.meta.api.core.configure.yaml;

import org.springframework.core.convert.converter.Converter;

/**
 * yaml类型转换器获取类
 * <p>
 * 2020/6/8 4:50 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface SpringTypeConverterRegister {

    /**
     * 注册
     *
     * @return 类型转换器
     */
    @SuppressWarnings("rawtypes")
    Converter register();

}
