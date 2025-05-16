package pro.shushi.pamirs.core.common.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Adamancy Zhang on 2021-05-09 10:27
 */
public abstract class AbstractBaseTest {

    private static final AtomicInteger aci = new AtomicInteger(1);

    @Order(Integer.MIN_VALUE)
    @BeforeEach
    public void baseBefore() {
        System.out.printf("-------------------- 开始第%d次测试 --------------------\n", aci.get());
    }

    @Order(Integer.MAX_VALUE)
    @AfterEach
    public void baseAfter() {
        System.out.printf("-------------------- 第%s次测试结束 --------------------\n", aci.getAndIncrement());
    }
}
