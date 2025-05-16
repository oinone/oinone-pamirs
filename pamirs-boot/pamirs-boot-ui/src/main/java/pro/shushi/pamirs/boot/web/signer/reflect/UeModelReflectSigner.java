package pro.shushi.pamirs.boot.web.signer.reflect;

import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Optional;

/**
 * 视图模型签名器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
@SPI.Service(UeModel.MODEL_MODEL)
public class UeModelReflectSigner implements ModelReflectSigner<UeModel, Class> {

    @Override
    public String sign(MetaNames names, Class source) {
        Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(source, Model.model.class);
        return Optional.ofNullable(modelModelAnnotation).map(Model.model::value).orElse(source.getName());
    }

}
