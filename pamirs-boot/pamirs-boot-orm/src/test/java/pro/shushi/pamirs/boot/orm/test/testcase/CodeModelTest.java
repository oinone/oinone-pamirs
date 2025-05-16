package pro.shushi.pamirs.boot.orm.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.framework.test.data.base.model.TestCodeModel;

/**
 * CodeModel数据管理器测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("CodeModel数据管理器测试")
public class CodeModelTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("测试CodeModel数据管理器功能")
    @PamirsTransactional
    public void testDataOperation() {
        TestCodeModel result = query("test1").queryOne();
        if (null == result) {
            result = data("test1", "test1").create();
        }
        result.setName("testx");
        result.updateByCode();
        TestCodeModel updateResult = result.queryByCode();
        Assert.assertEquals("测试 CodeModel 失败", updateResult.getName(), result.getName());
        result = query("testx").queryOne();
        Assert.assertEquals("测试 CodeModel 失败", updateResult.getName(), result.getName());
        Boolean deleteResult = result.deleteByCode();
        Assert.assertEquals("测试 CodeModel 失败", true, deleteResult);
    }

    @SuppressWarnings("SameParameterValue")
    private TestCodeModel data(String code, String name) {
        return (TestCodeModel) new TestCodeModel().setName(name).setCode(code);
    }

    private TestCodeModel query(String name) {
        return new TestCodeModel().setName(name);
    }

}
