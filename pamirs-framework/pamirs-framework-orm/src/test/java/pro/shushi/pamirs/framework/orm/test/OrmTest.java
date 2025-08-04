package pro.shushi.pamirs.framework.orm.test;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.AbstractBaseTest;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.core.session.Sessions;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * ORM测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("ORM测试")
public class OrmTest extends AbstractBaseTest {

    @Resource
    private DataConverter persistenceDataConverter;

    @Test
    @Order(0)
    @DisplayName("测试一对多ORM")
    public void testOneToMany() {
        // 准备数据
        MetaData base = fetchMetaData(ModuleConstants.MODULE_BASE);
        Sessions.fillSession(Lists.newArrayList(base));

        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setType(ModelTypeEnum.STORE).setCreateDate(new Date());
        ModelField modelField1 = new ModelField();
        modelField1.setName("name1").setField("field1").setTtype(TtypeEnum.STRING).setCreateDate(new Date());
        ModelField modelField2 = new ModelField();
        modelField2.setName("name2").setField("field2").setTtype(TtypeEnum.TEXT).setCreateDate(new Date());
        modelDefinition.setModel("model");
        modelDefinition.setName("name");
        modelDefinition.setModelFields(Lists.newArrayList(
                modelField1, modelField2
        ));

        Map<String, Object> map = persistenceDataConverter.in(ModelDefinition.MODEL_MODEL, modelDefinition);
        Assert.assertEquals("测试枚举字段", "store", map.get("type"));
        ModelDefinition result = persistenceDataConverter.out(ModelDefinition.MODEL_MODEL, map);
        Assert.assertEquals("测试枚举字段", ModelTypeEnum.STORE, result.getType());
    }

    @Test
    @Order(0)
    @DisplayName("测试多对一ORM")
    public void testManyToOne() {
        // 准备数据
        MetaData base = fetchMetaData(ModuleConstants.MODULE_BASE);
        Sessions.fillSession(Lists.newArrayList(base));

        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setType(ModelTypeEnum.STORE).setCreateDate(new Date());
        ModelField modelField1 = new ModelField();
        modelField1.setName("name1").setField("field1").setTtype(TtypeEnum.STRING).setCreateDate(new Date());
        modelDefinition.setModel("model");
        modelDefinition.setName("name");
        modelDefinition.setOptimisticLocker(modelField1);

        Map<String, Object> map = persistenceDataConverter.in(ModelDefinition.MODEL_MODEL, modelDefinition);
        Assert.assertEquals("测试枚举字段", "store", map.get("type"));
        Assert.assertEquals("测试枚举字段", TtypeEnum.STRING, ((ModelField) map.get("optimisticLocker")).getTtype());
        ModelDefinition result = persistenceDataConverter.out(ModelDefinition.MODEL_MODEL, map);
        Assert.assertEquals("测试枚举字段", ModelTypeEnum.STORE, result.getType());
        Assert.assertEquals("测试枚举字段", TtypeEnum.STRING, result.getOptimisticLocker().getTtype());
    }

}
