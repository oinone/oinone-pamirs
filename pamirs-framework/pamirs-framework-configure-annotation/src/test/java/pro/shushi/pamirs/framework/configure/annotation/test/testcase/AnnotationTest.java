package pro.shushi.pamirs.framework.configure.annotation.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.util.Optional;
import java.util.Set;

/**
 * 注解测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("注解测试")
public class AnnotationTest {

    @Test
    @Order(1)
    @DisplayName("测试注解读取")
    public void testAnnotation() {
        time(() -> {
            @SuppressWarnings("unused") Set<Class<?>> classes = ClassUtils.getClassesByPacks("pro.shushi.pamirs.meta.model");
        }, "扫描类");

        time(() -> {
            for (int i = 0; i < 1000; i++) {
                AnnotationUtils.getAnnotation(ModelDefinition.class, Model.class);
            }
        }, "读取注解");

        time(() -> {
            for (int i = 0; i < 10000; i++) {
                ModelDefinition model = new ModelDefinition();
                //noinspection ResultOfMethodCallIgnored
                Optional.of(model).map(v -> v.setModel("123").getName()).orElse(null);
            }
        }, "Optional");

        time(() -> {
            for (int i = 0; i < 10000; i++) {
                ModelDefinition model = new ModelDefinition();
                //noinspection StatementWithEmptyBody
                if (null == model.setModel("123").getName()) {

                }
            }
        }, "get set");

    }

    private void time(TimeShow show, String title) {
        long t = System.currentTimeMillis();
        show.show();
        System.out.println(title + ":" + (System.currentTimeMillis() - t));
    }

    interface TimeShow {
        void show();
    }

}
