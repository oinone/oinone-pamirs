package pro.shushi.pamirs.meta.common.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.spi.path.SpiClassPathApi;
import pro.shushi.pamirs.meta.common.test.mock.spi.anno.TestAnnotationApi;
import pro.shushi.pamirs.meta.common.test.mock.spi.anno.TestAnnotationApiImpl2;
import pro.shushi.pamirs.meta.common.test.mock.spi.java.SpiClassPathApiImpl2;
import pro.shushi.pamirs.meta.common.test.mock.spi.spring.TestSpringApi;
import pro.shushi.pamirs.meta.common.test.mock.spi.spring.TestSpringApiImpl3;

/**
 * SPI同名重载测试类
 * <p>
 * 2020/8/4 5:14 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@DisplayName("SPI同名重载测试")
public class ExtensionServiceOverrideTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试Java SPI服务")
    public void testJavaExtensionService() {

        SpiClassPathApi javaExtension = ExtensionServiceLoader.getExtension(SpiClassPathApi.class, "p2");
        Assert.assertEquals("测试 Java SPI override", SpiClassPathApiImpl2.class, javaExtension.getClass());

        TestAnnotationApi annotationExtension = ExtensionServiceLoader.getExtension(TestAnnotationApi.class, "p2");
        Assert.assertEquals("测试 Annotation SPI override", TestAnnotationApiImpl2.class, annotationExtension.getClass());

        TestSpringApi extension = ExtensionServiceLoader.getExtension(TestSpringApi.class, "p2");
        Assert.assertEquals("测试 Spring SPI override", TestSpringApiImpl3.class, extension.getClass());

    }

}
