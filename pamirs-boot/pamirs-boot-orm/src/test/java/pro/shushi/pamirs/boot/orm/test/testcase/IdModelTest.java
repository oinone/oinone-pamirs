package pro.shushi.pamirs.boot.orm.test.testcase;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

/**
 * IdModel数据管理器测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("IdModel数据管理器测试")
public class IdModelTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("测试IdModel数据管理器功能")
    @PamirsTransactional
    public void testDataOperation() {
        ModelDefinition result = query("test1").queryOne();
        if (null == result) {
            result = data("test1", "test1").create();
        }
        result = result.queryById(result.getId());
        result.setName("testx");
        result.updateById();
        ModelDefinition updateResult = result.queryById();
        Assert.assertEquals("测试IdModel失败", updateResult.getName(), result.getName());
        result = query("testx").queryOne();
        Assert.assertEquals("测试IdModel失败", updateResult.getName(), result.getName());
        Boolean deleteResult = result.deleteById();
        Assert.assertEquals("测试IdModel失败", true, deleteResult);
    }

    @SuppressWarnings("SameParameterValue")
    private ModelDefinition data(String model, String name) {
        return new ModelDefinition().setModel(model).setName(name);
    }

    private ModelDefinition query(String name) {
        return new ModelDefinition().setName(name);
    }

}
