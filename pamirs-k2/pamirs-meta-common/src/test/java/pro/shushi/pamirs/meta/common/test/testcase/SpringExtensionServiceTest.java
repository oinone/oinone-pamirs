package pro.shushi.pamirs.meta.common.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.spi.ExtensionServiceLoader;
import pro.shushi.pamirs.meta.common.test.mock.spi.spring.TestSpringApi;
import pro.shushi.pamirs.meta.common.test.mock.spi.spring.TestSpringApiImpl1;

import java.util.List;
import java.util.Set;

/**
 * Spring SPI测试类
 * <p>
 * 2020/8/4 5:14 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@DisplayName("Spring SPI测试")
public class SpringExtensionServiceTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("测试Spring SPI服务")
    public void testSpringExtensionService() {
        // 准备测试数据
        String extensionName1 = "p1";
        String extensionName2 = "p2";

        // 执行用例与断言
        TestSpringApi extension = ExtensionServiceLoader.getExtension(TestSpringApi.class, extensionName1);
        List<String> paths = extension.path();
        Assert.assertEquals("测试SPI调用", PackageConstants.PACKAGE_PAMIRS, paths.get(0));
        paths = extension.path();
        Assert.assertEquals("测试二次调用缓存生效", PackageConstants.PACKAGE_PAMIRS, paths.get(0));
        String extensionName = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).getExtensionName(extension);
        Assert.assertEquals("测试获取扩展名", extensionName1, extensionName);
        List<TestSpringApi> apiList = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).getOrderedExtensions();
        Assert.assertEquals("测试获取SPI的所有实现", 2, apiList.size());
        Assert.assertEquals("测试优先级", TestSpringApiImpl1.class, apiList.get(0).getClass());
        Set<String> extensionNameSet = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).getSupportedExtensions();
        Assert.assertEquals("测试获取扩展名set", extensionName1, extensionNameSet.iterator().next());
        boolean isExist = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).hasExtension(extensionName2);
        Assert.assertTrue("测试扩展是否存在", isExist);
        TestSpringApi defaultExtension = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).getDefaultExtension();
        Assert.assertEquals("测试获取默认扩展", TestSpringApiImpl1.class, defaultExtension.getClass());
        TestSpringApi highPriorityExtension = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).getExtension();
        Assert.assertEquals("测试获取最高优先级扩展", TestSpringApiImpl1.class, highPriorityExtension.getClass());
        Assert.assertEquals("测试扩展自动填充", "p1", ((TestSpringApiImpl1)highPriorityExtension).value);
        ExtensionServiceLoader.resetExtensionLoader(TestSpringApi.class);
        isExist = ExtensionServiceLoader.getExtensionLoader(TestSpringApi.class).hasExtension(extensionName1);
        Assert.assertTrue("测试重置扩展加载器", isExist);

    }

}
