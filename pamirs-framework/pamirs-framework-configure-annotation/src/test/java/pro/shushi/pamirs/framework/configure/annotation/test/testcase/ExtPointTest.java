package pro.shushi.pamirs.framework.configure.annotation.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import pro.shushi.pamirs.framework.configure.annotation.test.mock.right.extpoint.TestInheritedExtPointInstance;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.Function;

import java.lang.reflect.Method;

/**
 * 扩展点测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("扩展点测试")
public class ExtPointTest {

    @Test
    @Order(1)
    @DisplayName("测试扩展点")
    public void testExtPoint(){
        Class<?> testClass = TestInheritedExtPointInstance.class;
        Method[] methods = testClass.getDeclaredMethods();
        Function.fun funAnnotation = AnnotatedElementUtils.findMergedAnnotation(methods[0], Function.fun.class);
        ExtPoint.Implement extImpl = AnnotatedElementUtils.findMergedAnnotation(methods[0], ExtPoint.Implement.class);
        assert funAnnotation != null;
        Assert.assertEquals("annotation configure convert:extPoint", "testInherited", funAnnotation.value());
        assert extImpl != null;
        Assert.assertEquals("annotation configure convert:extPointImpl", 3, extImpl.priority());
    }

}
