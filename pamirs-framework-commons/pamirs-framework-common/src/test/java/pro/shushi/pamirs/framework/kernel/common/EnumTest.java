package pro.shushi.pamirs.framework.kernel.common;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.AbstractBaseTest;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * 模型和枚举基本测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("模型和枚举基本测试")
public class EnumTest extends AbstractBaseTest {

    @Test
    @Order(1)
    @DisplayName("枚举类测试")
    public void testValueOf() {
        JsonUtils.parseObject(JsonUtils.toJSONString(TtypeEnum.INTEGER), TtypeEnum.class);
        Assert.assertEquals("ModelTypeEnum.valueOf STORE", "STORE", ModelTypeEnum.valueOf("STORE").name());
        Assert.assertEquals("ModelTypeEnum.valueOf STORE", "STORE", ModelTypeEnum.valueOf("STORE").name());
        Assert.assertEquals("ModelTypeEnum.valueFor store", "store", Enums.getEnumByValue(ModelTypeEnum.class, "store").value());
        Assert.assertEquals("ModelTypeEnum.values", 4, ModelTypeEnum.values().length);
        Assert.assertEquals("ModelTypeEnum.ordinal", 2, ModelTypeEnum.ABSTRACT.ordinal());
        Assert.assertEquals("ModelTypeEnum.ordinal", 2, ModelTypeEnum.ABSTRACT.ordinal());
        Assert.assertEquals("ModelTypeEnum.valueOf ModelTypeEnum.class ABSTRACT", "ABSTRACT", ModelTypeEnum.valueOf(ModelTypeEnum.class, "ABSTRACT").name());
    }

    @Test
    @Order(2)
    @DisplayName("继承枚举类测试")
    public void testInheritedEnum() {
        Assert.assertTrue("TtypeEnum isAssignableFrom TtypeEnum.M2M", RtypeEnum.class.isAssignableFrom(RtypeEnum.M2M.getClass()));
        Assert.assertTrue("TtypeEnum isAssignableFrom TtypeEnum.M2M", RtypeEnum.class.isAssignableFrom(TtypeEnum.M2M.getClass()));
    }

    @Test
    @Order(3)
    @DisplayName("枚举重载与序号测试")
    public void testOverride() {
        TtypeEnum.getEnumList(TtypeEnum.class);
        Assert.assertEquals("TtypeEnum.M2M ordinal", 15, TtypeEnum.M2M.ordinal());
        Assert.assertEquals("RtypeEnum.M2M ordinal", 15, RtypeEnum.M2M.ordinal());
        Assert.assertEquals("TtypeEnum与RtypeEnum序号一致", TtypeEnum.M2M.ordinal(), RtypeEnum.M2M.ordinal());
        Assert.assertEquals("TtypeEnum与RtypeEnum排序一致",
                TtypeEnum.M2M.ordinal() - RtypeEnum.M2O.ordinal(),
                TtypeEnum.M2M.ordinal() - RtypeEnum.M2O.ordinal());
    }

}
