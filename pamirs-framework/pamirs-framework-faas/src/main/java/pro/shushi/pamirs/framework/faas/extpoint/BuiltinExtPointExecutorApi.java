package pro.shushi.pamirs.framework.faas.extpoint;

import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 内置扩展点处理器接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface BuiltinExtPointExecutorApi {

    Object before(Function function, Object... args);

    Object override(Function function, java.util.function.Function<Object[], Object> consumer, Object... args);

    Object callback(Function function, Object... args);

    Object after(Function function, Object ret);

}
