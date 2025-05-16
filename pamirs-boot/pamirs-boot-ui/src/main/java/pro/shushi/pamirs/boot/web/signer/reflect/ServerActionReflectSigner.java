package pro.shushi.pamirs.boot.web.signer.reflect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 服务器动作签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("unused")
@Slf4j
@SPI.Service(ServerAction.MODEL_MODEL)
public class ServerActionReflectSigner implements ModelReflectSigner<ServerAction, Method> {

    @Override
    public String sign(MetaNames names, Method source) {
        Fun funAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Fun.class);
        Function functionAnnotation = AnnotationUtils.getAnnotation(source, Function.class);
        Model.model modelAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Model.model.class);
        String model = Optional.ofNullable(modelAnnotation).map(Model.model::value).filter(StringUtils::isNotBlank).orElse(null);
        String namespace = Optional.ofNullable(funAnnotation).map(Fun::value).filter(StringUtils::isNotBlank).orElse(source.getDeclaringClass().getName());
        namespace = Optional.ofNullable(model).orElse(namespace);
        String name = Optional.ofNullable(functionAnnotation).map(Function::name).filter(StringUtils::isNotBlank).orElse(source.getName());
        names.setModel(namespace);
        return namespace + CharacterConstants.SEPARATOR_DOT + name;
    }

}
