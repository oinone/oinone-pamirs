package pro.shushi.pamirs.meta.api.core.orm.systems.enums;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.ErrorsProcessor;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ErrorDefinition;
import pro.shushi.pamirs.meta.domain.model.ErrorsDefinition;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.*;

/**
 * 错误处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@Slf4j
@SuppressWarnings({"rawtypes"})
@SPI.Service
public class BaseErrorsProcessor implements ErrorsProcessor<ErrorsDefinition> {

    @Override
    public ErrorsDefinition fetchErrorsFromEnum(String module, Class enumClass) {
        return fillErrorsFromEnum(new ErrorsDefinition(), module, enumClass);
    }

    @Override
    public ErrorsDefinition fillErrorsFromEnum(ErrorsDefinition errorsDefinition, String module, Class enumClass) {
        if (!TypeUtils.isIEnumClass(enumClass)) {
            throw PamirsException.construct(BASE_ERRORS_CONFIG_ERROR).appendMsg(enumClass.getName()).errThrow();
        }
        String clazz = enumClass.getName();
        SystemSourceEnum source = Optional.ofNullable(AnnotationUtils.getAnnotation(enumClass, Base.class))
                .map(Base::value).orElse(null);
        Errors errors = AnnotationUtils.getAnnotation(enumClass, Errors.class);
        String displayName = Optional.ofNullable(errors).map(Errors::displayName).orElse(enumClass.getSimpleName());
        String summary = Optional.ofNullable(errors).map(Errors::summary).orElse(null);
        errorsDefinition.setDisplayName(displayName)
                .setSummary(summary)
                .setClazz(clazz)
                .setModule(module)
                .setSystemSource(source)
        ;
        return errorsDefinition;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ErrorDefinition> fetchErrorDefinitionMap(Map<String, ErrorDefinition> errorDefinitionMap,
                                                                String module, Class errorEnumClass) {
        Map<String, ErrorDefinition> result = new HashMap<>();
        try {
            SystemSourceEnum source = Optional.ofNullable(AnnotationUtils.getAnnotation(errorEnumClass, Base.class))
                    .map(Base::value).orElse(null);
            if (!ExpBaseEnum.class.isAssignableFrom(errorEnumClass)) {
                throw PamirsException.construct(BASE_ERROR_ENUM_TYPE_ERROR_ERROR).appendMsg("class:" + errorEnumClass).errThrow();
            }
            String errorClazz = errorEnumClass.getName();
            ErrorDefinition errorDefinition;
            List<IEnum> enums = Enums.getEnumList((Class<IEnum>) errorEnumClass);
            for (IEnum one : enums) {
                String errorSign = errorClazz + CharacterConstants.SEPARATOR_DOT + one.name();
                errorDefinition = errorDefinitionMap.get(errorSign);
                if (null == errorDefinition) {
                    errorDefinition = new ErrorDefinition();
                }
                errorDefinition.setName(one.name())
                        .setClazz(errorClazz)
                        .setCode(TypeUtils.stringValueOf(one.value()))
                        .setMsg(one.help())
                        .setType(ErrorTypeEnum.valueOf(((ExpBaseEnum) one).type().name()))
                        .setState(ActiveEnum.ACTIVE)
                        .setSystemSource(source);
                result.put(errorSign, errorDefinition);
            }
        } catch (Exception e) {
            throw PamirsException.construct(BASE_ERROR_ENUM_CONFIG_ERROR, e).errThrow();
        }
        return result;
    }

}
