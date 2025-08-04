package pro.shushi.pamirs.framework.connectors.data.ddl.test.testcase;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.framework.connectors.data.api.ddl.TableComputer;
import pro.shushi.pamirs.framework.connectors.data.ddl.test.mock.model.TestManyToManyModel;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 生成多对多表
 * <p>
 * 2020/8/21 5:50 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@DisplayName("生成多对多表结构")
public class DdlGeneratorTest extends AbstractBaseTest {


    @Resource
    private TableComputer tableComputer;

    @Test
    @Order(0)
    @DisplayName("生成表")
    public void testCreateTable() {
        // 执行测试用例
        ModelConfig modelConfig = Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(TestManyToManyModel.MODEL_MODEL);
        List<String> ddl = tableComputer.compute(null, null, modelConfig, null).getDdl();
        System.out.println(StringUtils.join(ddl, CharacterConstants.SEPARATOR_EMPTY));
    }

}
