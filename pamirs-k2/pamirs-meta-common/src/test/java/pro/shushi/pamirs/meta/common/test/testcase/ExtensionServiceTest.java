package pro.shushi.pamirs.meta.common.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.spi.path.SpiClassPathApi;
import pro.shushi.pamirs.meta.common.spi.path.SpiClassPathApiImpl;
import pro.shushi.pamirs.meta.common.test.mock.spi.anno.TestAnnotationApi;
import pro.shushi.pamirs.meta.common.test.mock.spi.anno.TestAnnotationApiImpl1;
import pro.shushi.pamirs.meta.common.test.mock.spi.anno.TestAnnotationApiImpl2;

import java.util.List;
import java.util.Set;

/**
 * SPI测试类
 * <p>
 * 2020/8/4 5:14 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@DisplayName("SPI测试")
public class ExtensionServiceTest {

    @Test
    @Order(1)
    @DisplayName("测试Java SPI服务")
    public void testJavaExtensionService() {
        SpiClassPathApi extension = ExtensionServiceLoader.getExtension(SpiClassPathApi.class, SpiClassPathApiImpl.class.getName());
        List<String> paths = extension.path();
        Assert.assertEquals("测试SPI调用", PackageConstants.PACKAGE_PAMIRS, paths.get(0));
        paths = extension.path();
        Assert.assertEquals("测试二次调用缓存生效", PackageConstants.PACKAGE_PAMIRS, paths.get(0));
        String extensionName = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).getExtensionName(extension);
        Assert.assertEquals("测试获取扩展名", SpiClassPathApiImpl.class.getName(), extensionName);
        List<SpiClassPathApi> apiList = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).getOrderedExtensions();
        Assert.assertEquals("测试获取SPI的所有实现", 3, apiList.size());
        Assert.assertEquals("测试优先级", pro.shushi.pamirs.meta.common.test.mock.spi.java.SpiClassPathApiImpl2.class, apiList.get(0).getClass());
        Set<String> extensionNameSet = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).getSupportedExtensions();
        Assert.assertEquals("测试获取扩展名set", "p2", extensionNameSet.iterator().next());
        boolean isExist = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class)
                .hasExtension(pro.shushi.pamirs.meta.common.test.mock.spi.java.SpiClassPathApiImpl.class.getName());
        Assert.assertTrue("测试扩展是否存在", isExist);
        SpiClassPathApi defaultExtension = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).getDefaultExtension();
        Assert.assertNull("测试获取默认扩展", defaultExtension);
        SpiClassPathApi highPriorityExtension = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).getExtension();
        Assert.assertEquals("测试获取最高优先级扩展", pro.shushi.pamirs.meta.common.test.mock.spi.java.SpiClassPathApiImpl2.class,
                highPriorityExtension.getClass());
        Assert.assertEquals("测试扩展自动填充", "p2",
                ((pro.shushi.pamirs.meta.common.test.mock.spi.java.SpiClassPathApiImpl2) highPriorityExtension).value);
        ExtensionServiceLoader.resetExtensionLoader(SpiClassPathApi.class);
        isExist = ExtensionServiceLoader.getExtensionLoader(SpiClassPathApi.class).hasExtension(SpiClassPathApiImpl.class.getName());
        Assert.assertTrue("测试重置扩展加载器", isExist);

    }

    @Test
    @Order(1)
    @DisplayName("测试Annotation SPI服务")
    public void testAnnotationExtensionService() {
        // 准备测试数据
        String extensionName1 = "p1";
        String extensionName2 = "p2";

        // 执行用例与断言
        TestAnnotationApi extension = ExtensionServiceLoader.getExtension(TestAnnotationApi.class, extensionName1);
        List<String> paths = extension.path();
        Assert.assertEquals("测试SPI调用", PackageConstants.PACKAGE_PAMIRS, paths.get(0));
        paths = extension.path();
        Assert.assertEquals("测试二次调用缓存生效", PackageConstants.PACKAGE_PAMIRS, paths.get(0));
        String extensionName = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).getExtensionName(extension);
        Assert.assertEquals("测试获取扩展名", extensionName1, extensionName);
        List<TestAnnotationApi> apiList = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).getOrderedExtensions();
        Assert.assertEquals("测试获取SPI的所有实现", 2, apiList.size());
        Assert.assertEquals("测试优先级", TestAnnotationApiImpl2.class, apiList.get(0).getClass());
        Set<String> extensionNameSet = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).getSupportedExtensions();
        Assert.assertEquals("测试获取扩展名set", extensionName1, extensionNameSet.iterator().next());
        boolean isExist = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).hasExtension(extensionName2);
        Assert.assertTrue("测试扩展是否存在", isExist);
        TestAnnotationApi defaultExtension = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).getDefaultExtension();
        Assert.assertEquals("测试获取默认扩展", TestAnnotationApiImpl1.class, defaultExtension.getClass());
        TestAnnotationApi highPriorityExtension = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).getExtension();
        Assert.assertEquals("测试获取最高优先级扩展", TestAnnotationApiImpl2.class, highPriorityExtension.getClass());
        Assert.assertEquals("测试扩展自动填充", "p1", ((TestAnnotationApiImpl1) extension).value);
        ExtensionServiceLoader.resetExtensionLoader(TestAnnotationApi.class);
        isExist = ExtensionServiceLoader.getExtensionLoader(TestAnnotationApi.class).hasExtension(extensionName1);
        Assert.assertTrue("测试重置扩展加载器", isExist);

    }

}
