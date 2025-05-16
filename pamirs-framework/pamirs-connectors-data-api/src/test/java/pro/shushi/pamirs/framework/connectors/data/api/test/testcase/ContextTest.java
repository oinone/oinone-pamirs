package pro.shushi.pamirs.framework.connectors.data.api.test.testcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;

/**
 * 上下文测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("上下文测试")
public class ContextTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("测试上下文生成")
    public void testDeleteByPk() {

        Map<String, Object> data = Spider.getDefaultExtension(TableNameComputer.class).context(new ModelDefinition().setModule("test"));

        AssertionErrors.assertEquals("删除单条失败", "test", data.get("module"));

    }

}
