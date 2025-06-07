package pro.shushi.pamirs.framework.faas.extpoint;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.faas.ExtPointApi;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.constant.ExtPointConstants;

import javax.annotation.Resource;

/**
 * 内置扩展点处理器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Component
public class BuiltinExtPointExecutor implements BuiltinExtPointExecutorApi {

    @Resource
    private ExtPointApi extPointApi;

    @Override
    public Object before(Function function, Object... args) {
        String namespace = function.getNamespace();
        String extPointName = function.getFun() + ExtPointConstants.BEFORE_SUFFIX;
        return extPointApi.run(namespace, extPointName, args);
    }

    @Override
    public Object override(Function function, java.util.function.Function<Object[], Object> consumer, Object... args) {
        String namespace = function.getNamespace();
        String extPointName = function.getFun() + ExtPointConstants.OVERRIDE;
        return extPointApi.run(namespace, extPointName, consumer, args);
    }

    @Override
    public Object callback(Function function, Object... args) {
        String namespace = function.getNamespace();
        String extPointName = function.getFun() + ExtPointConstants.CALLBACK;
        return extPointApi.run(namespace, extPointName, args);
    }

    @Override
    public Object after(Function function, Object ret) {
        String namespace = function.getNamespace();
        String extPointName = function.getFun() + ExtPointConstants.AFTER_SUFFIX;
        return extPointApi.run(namespace, extPointName, ret);
    }

}
