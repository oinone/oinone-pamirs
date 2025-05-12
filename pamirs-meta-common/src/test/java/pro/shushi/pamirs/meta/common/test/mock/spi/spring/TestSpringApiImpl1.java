package pro.shushi.pamirs.meta.common.test.mock.spi.spring;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.common.spi.AutoFill;

import java.util.List;

import static pro.shushi.pamirs.meta.common.constants.PackageConstants.PACKAGE_PAMIRS;

/**
 * SPI类路径API实现
 * <p>
 * 2020/8/4 5:38 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(10)
@Component("p1")
public class TestSpringApiImpl1 implements TestSpringApi {

    @Override
    public List<String> path() {
        return Lists.newArrayList(PACKAGE_PAMIRS);
    }

    public String value;

    @SuppressWarnings("unused")
    @AutoFill
    public void setValue(){
        value = "p1";
    }

}
