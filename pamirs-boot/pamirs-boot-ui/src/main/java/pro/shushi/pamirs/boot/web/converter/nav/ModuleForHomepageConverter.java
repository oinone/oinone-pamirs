package pro.shushi.pamirs.boot.web.converter.nav;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * 模块首页配置注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class ModuleForHomepageConverter implements ModelConverter<ModuleDefinition, Class> {

    @Override
    public int priority() {
        return 2;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxHomepage homepageAnnotation = AnnotationUtils.getAnnotation(source, UxHomepage.class);
        Result result = new Result();
        if (null == homepageAnnotation) {
            return result.error();
        }
        if (StringUtils.isBlank(homepageAnnotation.value().model())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .append(MessageFormat.format("请为应用首页配置跳转视图的模型，class:{0}", source.getName())));
            result.error();
            context.error().broken();
        }
        return result;
    }

    @Override
    public ModuleDefinition convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, ModuleDefinition metaModelObject) {
        Module.module moduleModuleAnnotation = AnnotationUtils.getAnnotation(source, Module.module.class);
        UxHomepage homepageAnnotation = AnnotationUtils.getAnnotation(source, UxHomepage.class);
        metaModelObject.setDefaultHomePageModel(Objects.requireNonNull(homepageAnnotation).value().model())
                .setDefaultHomePageName(StringUtils.isBlank(homepageAnnotation.actionName()) ? ViewActionConstants.homepage.name : homepageAnnotation.actionName())
                .setModule(Optional.ofNullable(moduleModuleAnnotation).map(Module.module::value).orElse(names.getModule()))
                .setName(names.getModuleName())
        ;

        return metaModelObject;
    }

    @Override
    public String group() {
        return ModuleDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ModuleDefinition.class;
    }

}
