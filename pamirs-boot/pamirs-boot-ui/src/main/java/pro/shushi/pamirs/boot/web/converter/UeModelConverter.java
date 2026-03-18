package pro.shushi.pamirs.boot.web.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * 模型前端配置注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class UeModelConverter implements ModelConverter<UeModel, Class> {

    @Override
    public int priority() {
        return 1;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        pro.shushi.pamirs.meta.annotation.Model modelAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.Model.class);
        Result result = new Result();
        if (null == modelAnnotation) {
            return result.error();
        }

        Model.Static modelStaticAnnotation = AnnotationUtils.getAnnotation(source, Model.Static.class);
        if (null != modelStaticAnnotation) {
            return result.error();
        }

        if (0 == modelAnnotation.labelFields().length && StringUtils.isBlank(modelAnnotation.label())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                    .append(I18nUtils.getMessage("UeModelConverter.labelMissing", source.getName())));
        }
        return result;
    }

    @Override
    public UeModel convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, UeModel metaModelObject) {
        pro.shushi.pamirs.meta.annotation.Model modelAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.Model.class);
        Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Model.Advanced.class);
        // 模型编码
        String modelModel = names.getModel();
        // 填充模型
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);
        String name = Optional.ofNullable(modelAdvancedAnnotation).map(Model.Advanced::name).filter(StringUtils::isNotBlank)
                .map(StringUtils::uncapitalize).orElse(PStringUtils.camelCaseFromModel(modelModel));
        metaModelObject.setLabelFields(PStringUtils.trim(Objects.requireNonNull(modelAnnotation).labelFields()))
                .setLabel(I18nUtils.translateModel(names.getModule(), modelModel, "label", StringUtils.defaultIfBlank(modelAnnotation.label(), null)))
                .setModel(modelModel)
                .setName(name)
                .setSystemSource(systemSource)
        ;

        return metaModelObject;
    }

    @Override
    public String group() {
        return UeModel.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return UeModel.class;
    }

}
