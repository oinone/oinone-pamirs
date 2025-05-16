package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.annotation.core.check.MetaUniqueChecker;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.text.MessageFormat;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_DATA_DICTIONARY_HAS_FUN_ERROR;

/**
 * 数据字典注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Component
@SuppressWarnings({"rawtypes", "unused"})
public class DictionaryConverter implements ModelConverter<DataDictionary, Class> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        Result result = new Result();
        Dict dictAnnotation = AnnotationUtils.getAnnotation(source, Dict.class);
        if (null == dictAnnotation || !TypeUtils.isIEnumClass(source)) {
            context.broken();
            return result.error();
        }
        {
            Fun funAnnotation = AnnotationUtils.getAnnotation(source, Fun.class);
            if (null != funAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_DATA_DICTIONARY_HAS_FUN_ERROR)
                        .append(MessageFormat
                                .format("请不要在枚举类上配置@Fun注解，class:{0}",
                                        source.getName())));
                context.error().broken();
                return result.error();
            }
        }
        // 数据字典编码重复检查
        @SuppressWarnings("unchecked")
        String dictionary = Spider.getExtension(ModelReflectSigner.class, DataDictionary.MODEL_MODEL).sign(names, source);
        MetaUniqueChecker.check(context, result, DataDictionary.MODEL_MODEL, dictionary, source.getName());
        return result;
    }

    @Override
    public DataDictionary convert(MetaNames names, Class source, DataDictionary dataDictionary) {
        String module = names.getModule();
        Models.enums().fillDataDictionaryFromEnum(dataDictionary, module, source);
        return dataDictionary;
    }

    @Override
    public String group() {
        return DataDictionary.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return DataDictionary.class;
    }

}
