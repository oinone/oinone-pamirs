package pro.shushi.pamirs.framework.configure.annotation.core.sign.clazz;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

import java.util.Optional;

/**
 * 数据字典签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@SPI.Service(DataDictionary.MODEL_MODEL)
public class DictionaryReflectSigner implements ModelReflectSigner<DataDictionary, Class> {

    @Override
    public String sign(MetaNames names, Class source) {
        return Optional.ofNullable(AnnotationUtils.getAnnotation(source, Dict.class))
                .map(Dict::dictionary).filter(StringUtils::isNotBlank).orElse(
                        Optional.ofNullable(AnnotationUtils.getAnnotation(source, Dict.dictionary.class))
                                .map(Dict.dictionary::value)
                                .filter(StringUtils::isNotBlank)
                                .orElse(source.getName())
                );

    }

}
