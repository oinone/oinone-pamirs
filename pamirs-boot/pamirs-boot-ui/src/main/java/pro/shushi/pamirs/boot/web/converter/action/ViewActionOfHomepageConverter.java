package pro.shushi.pamirs.boot.web.converter.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.constants.ViewActionConstants;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.base.ux.annotation.navigator.UxHomepage;
import pro.shushi.pamirs.boot.web.utils.ActionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页窗口动作前端配置注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class ViewActionOfHomepageConverter implements ModelConverter<Map<String, ViewAction>, Class> {

    @Override
    public int priority() {
        return 202;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        UxHomepage homepageAnnotation = AnnotationUtils.getAnnotation(source, UxHomepage.class);
        Result result = new Result();
        if (null == homepageAnnotation || StringUtils.isBlank(homepageAnnotation.value().model())) {
            return result.error();
        }
        return result;
    }

    @Override
    public Map<String, ViewAction> convert(MetaNames names, @SuppressWarnings("rawtypes") Class source, Map<String, ViewAction> metaModelObject) {
        UxHomepage homepageAnnotation = AnnotationUtils.getAnnotation(source, UxHomepage.class);
        Map<String, ViewAction> viewActionMap = new HashMap<>();
        if (null != homepageAnnotation) {
            String actionName = homepageAnnotation.actionName();
            // 指定了actionName,忽略
            if (StringUtils.isBlank(actionName)) {
                ViewAction viewAction = new ViewAction();
                viewAction.setModel(homepageAnnotation.value().model());
                viewAction.setName(ViewActionConstants.homepage.name);
                @SuppressWarnings("unchecked")
                String sign = Spider.getExtension(ModelSigner.class, ViewAction.MODEL_MODEL).sign(viewAction);
                if (!metaModelObject.containsKey(sign)) {
                    viewActionMap.put(sign, viewAction);
                } else {
                    viewAction = metaModelObject.get(sign).disableMetaCompleted();
                }
                ActionUtils.configViewAction(viewAction, homepageAnnotation.value());
                viewAction.setDisplayName(ViewActionConstants.homepage.displayName);
                viewAction.setTitle(ViewActionConstants.homepage.title);
                viewAction.setLabel(viewAction.getDisplayName());
                viewAction.setModule(names.getModule());
                viewAction.setModuleName(names.getModuleName());
                viewAction.setSign(sign);
                viewAction.setContextType(ActionContextTypeEnum.CONTEXT_FREE);
            }
        }
        return viewActionMap;
    }

    @Override
    public String group() {
        return ViewAction.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ViewAction.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }


    @Override
    public List<String> signs(MetaNames names, Class source) {
        UxHomepage homepageAnnotation = AnnotationUtils.getAnnotation(source, UxHomepage.class);
        List<String> signs = new ArrayList<>();
        if (null != homepageAnnotation) {
            ViewAction viewAction = new ViewAction();
            viewAction.setModel(homepageAnnotation.value().model());
            viewAction.setName(StringUtils.isBlank(homepageAnnotation.actionName()) ? ViewActionConstants.homepage.name : homepageAnnotation.actionName());
            @SuppressWarnings("unchecked")
            String sign = Spider.getExtension(ModelSigner.class, ViewAction.MODEL_MODEL).sign(viewAction);
            signs.add(sign);
        }
        return signs;
    }

}
