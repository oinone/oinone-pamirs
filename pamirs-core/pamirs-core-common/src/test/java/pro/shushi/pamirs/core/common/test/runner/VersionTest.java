package pro.shushi.pamirs.core.common.test.runner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;
import pro.shushi.pamirs.core.common.exception.IncomparableException;
import pro.shushi.pamirs.core.common.version.Version;

/**
 * @author Adamancy Zhang on 2021-06-07 14:07
 */
@DisplayName("版本模型测试")
public class VersionTest extends AbstractBaseTest {

    @DisplayName("解析测试")
    @Test
    public void parseTest() {
        Version version = new Version("v1.0.0");
        assert version.getPrefix().equals("v");
        assert version.getSuffix().equals("");
        assert version.getVersion().equals("1.0.0");

        version = new Version("1.0.0-SNAPSHOT");
        assert version.getPrefix().equals("");
        assert version.getSuffix().equals("-SNAPSHOT");
        assert version.getVersion().equals("1.0.0");
    }

    @DisplayName("比较测试")
    @Test
    public void compareTest() {
        Version version = new Version("v1.0.0");

        // 小于
        assert version.compareTo(new Version("v1.0.1")) < 0;

        // 大于
        assert version.compareTo(new Version("v0.9.9")) > 0;

        // 等于
        assert version.compareTo(new Version("v1.0.0")) == 0;
        assert version.equals(new Version("v1.0.0"));

        // 不等于
        assert !version.equals(new Version("v1.1.0"));

        // 不可比较
        try {
            assert version.compareTo(new Version("1.0.0")) == 0;
        } catch (Throwable e) {
            if (e instanceof IncomparableException) {
                System.out.println(e.getMessage());
            } else {
                throw e;
            }
        }
        assert !version.equals(new Version("1.0.0"));
    }
}
