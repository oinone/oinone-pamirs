package pro.shushi.pamirs.framework.configure.annotation.core.annotation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.core.configure.clazz.ClazzScanner;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 类扫描器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Component
public class DefaultClazzScanner implements ClazzScanner {

    @Override
    public Set<Class<?>> scan(String... packages) {
        Set<Class<?>> classes = ClassUtils.getClassesByPacks(packages);
        Set<Class<?>> validClasses = new HashSet<>();
        for (Class clazz : classes) {
            if (clazz.isMemberClass()) {
                continue;
            }
            Model modelAnnotation = AnnotationUtils.getAnnotation(clazz, Model.class);
            Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(clazz, Model.model.class);
            Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(clazz, Model.Advanced.class);
        }
        return validClasses;
    }

}
