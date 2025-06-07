package pro.shushi.pamirs.boot.orm.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.boot.orm.test.mock.model.TestInheritedReentryModel;
import pro.shushi.pamirs.framework.connectors.data.tx.interceptor.PamirsTransactional;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.meta.constant.FunctionConstants.createOrUpdateBatch;

/**
 * 继承方法重入测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("继承方法重入测试")
public class InheritedReentryModelTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("测试继承方法重入")
    @PamirsTransactional
    public void testDataOperation() {
        Fun.run(TestInheritedReentryModel.MODEL_MODEL, createOrUpdateBatch, new ArrayList() {{
            this.add(new TestInheritedReentryModel().setName("test"));
        }});
    }

    @Test
    @Order(0)
    @DisplayName("测试前端方法重入")
    @PamirsTransactional
    public void testFrontendRequest() {
        List<TestInheritedReentryModel> list = new ArrayList() {{
            this.add(new TestInheritedReentryModel().setName("test"));
        }};
        Models.directive().request(() -> Fun.run(TestInheritedReentryModel.MODEL_MODEL, createOrUpdateBatch, list));
    }

}
