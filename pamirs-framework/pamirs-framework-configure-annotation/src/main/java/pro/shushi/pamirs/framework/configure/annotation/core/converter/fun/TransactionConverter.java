package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.TxConfigFetchApi;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.TransactionConfig;
import pro.shushi.pamirs.meta.util.MethodUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 事务注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Component
public class TransactionConverter implements ModelConverter<TransactionConfig, Method> {

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        if (MethodUtils.isInterface(source)) {
            return result.error();
        }
        Function functionAnnotation = AnnotationUtils.getAnnotation(source, Function.class);
        Action actionAnnotation = AnnotationUtils.getAnnotation(source, Action.class);
        if (null == functionAnnotation && null == actionAnnotation) {
            return result.error();
        }
        return result;
    }

    @Override
    public TransactionConfig convert(MetaNames names, Method method, TransactionConfig txConfig) {
        TxConfigFetchApi txConfigFetchApi = Spider.getDefaultExtension(TxConfigFetchApi.class);
        boolean supportTx = null != txConfigFetchApi;
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        return supportTx ? Optional.ofNullable(txConfigFetchApi.fetchTx(method, txConfig))
                .map(v -> v.setNamespace(namespace)).map(v -> v.setFun(fun)).orElse(null) : null;
    }

    @Override
    public String group() {
        return TransactionConfig.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return TransactionConfig.class;
    }

}
