package pro.shushi.pamirs.meta.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.ModelAttributeConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 模型工具类
 * <p>
 * 2021/2/6 10:56 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class ModelUtils {

    public static Class<?> fetchClazz(ModelDefinition data) {
        String clazzName = data.getLname();
        if (StringUtils.isBlank(clazzName) || SystemSourceEnum.UI.equals(data.getSystemSource())) {
            return null;
        }
        Class<?> clazz;
        try {
            clazz = TypeUtils.getClass(clazzName);
        } catch (Exception e) {
            log.error(MessageFormat.format("Class {0} does not exist, please check if window action has dirty data, which will be cleaned after re-installation", clazzName), e);
            return null;
        }
        if (!TypeUtils.isModelClass(clazz)) {
            return null;
        }
        return clazz;
    }

    @Nullable
    public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, @Nullable Class<?> clazz) {
        return clazz == null ? null : (Class<?>) MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.SUPERCLASS).get(annotationType, MergedAnnotation::isDirectlyPresent).getSource();
    }

    public static String findProxyParent(@SuppressWarnings("unused") String model, Class<?> superClazz) {
        if (null != superClazz) {
            Model.model superModelModelAnnotation = AnnotationUtils.getAnnotation(superClazz, Model.model.class);
            Model.Advanced superModelAdvancedAnnotation = AnnotationUtils.getAnnotation(superClazz, Model.Advanced.class);
            String superModel = Optional.ofNullable(superModelModelAnnotation).map(Model.model::value).orElse(superClazz.getName());
            ModelTypeEnum superModelType = Optional.ofNullable(superModelAdvancedAnnotation).map(Model.Advanced::type).orElse(null);
            if (ModelTypeEnum.PROXY.equals(superModelType)) {
                String proxy = findProxyParent(superModel, superClazz.getSuperclass());
                if (StringUtils.isBlank(proxy)) {
                    return null;
                }
                return proxy;
            } else {
                return superModel;
            }
        } else {
            return null;
        }
    }

    public static boolean isMultiTableInherited(Class<?> clazz) {
        Model.MultiTableInherited multiTableInheritedAnnotation = AnnotationUtils.getAnnotation(clazz, Model.MultiTableInherited.class);
        return null != multiTableInheritedAnnotation;
    }

    public static boolean isChangeTableInherited(Class<?> clazz) {
        Model.ChangeTableInherited changeTableInheritedAnnotation = AnnotationUtils.getAnnotation(clazz, Model.ChangeTableInherited.class);
        return null != changeTableInheritedAnnotation;
    }

    public static boolean isChangeTableInherited(ModelDefinition modelDefinition) {
        Boolean isChangeTableInherited = (Boolean) modelDefinition.getAttribute(ModelAttributeConstants.CHANGE_TABLE_INHERITED);
        return null != isChangeTableInherited && isChangeTableInherited;
    }

    public static List<String> sortAndSplitNames(String... names) {
        List<String> result = new ArrayList<>(names.length);
        List<String> sortNames = sortNames(names);
        for (String name : sortNames) {
            result.add(PStringUtils.substringAfterLast(name, CharacterConstants.SEPARATOR_DOT));
        }
        return result;
    }

    public static List<String> sortNames(String... names) {
        List<String> sortNames = ListUtils.toList(names);
        sortNames.sort(String::compareTo);
        return sortNames;
    }

    public static String moduleAbbreviate(String module) {
        int len = 8;
        if (null == module) {
            return null;
        }
        if (module.length() <= len) {
            return module;
        }
        if (module.contains(CharacterConstants.SEPARATOR_UNDERLINE)) {
            module = StringUtils.substringAfter(module, CharacterConstants.SEPARATOR_UNDERLINE);
            if (module.length() <= len) {
                return module;
            }
        }
        return StringUtils.substring(module, 0, len);
    }

}
