package pro.shushi.pamirs.boot.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionCacheFactoryApi;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 扩展点测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@Configuration
@DisplayName("扩展点测试")
public class SpiTest extends AbstractBaseTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Test
    @Order(0)
    @DisplayName("测试扩展点")
    public void test() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        new MyThread().run();
    }

    static class MyThread implements Runnable { //实现Runnable接口
        public void run() {
//            ClassScanner.scan(new String[]{"pro.shushi.pamirs"}, SPI.Service.class);
            Spider.getDefaultExtension(SessionCacheFactoryApi.class).fetchHookCache();
            System.out.println("test");
        }
    }

}
