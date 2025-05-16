package pro.shushi.pamirs.meta.model.test.utils;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.model.test.AbstractBaseTest;
import pro.shushi.pamirs.meta.model.test.model.TestTypeModel;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类型工具测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("类型工具测试")
public class TypeUtilsTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("模型默认值测试")
    public void testSimple() {
        new ModelDefinition().setStaticConfig(true);
    }

    @Test
    @Order(0)
    @DisplayName("模型名称生成测试")
    public void testGenerateName() {
        Assert.assertEquals("model1", PStringUtils.camelCaseFromModel("test.Model1"));
    }

    @Test
    @Order(1)
    @DisplayName("枚举测试")
    public void testEnum() {
        int ordinal = FunctionSourceEnum.EXTPOINT.ordinal();
        Assert.assertEquals(1, ordinal);
        Assert.assertEquals(TtypeEnum.M2O, RtypeEnum.M2O);

        // 枚举属性值方法
        //noinspection EqualsWithItself
        Assert.assertEquals(1, TtypeEnum.INTEGER.compareTo(TtypeEnum.INTEGER));
        Assert.assertEquals(-1, TtypeEnum.INTEGER.compareTo(TtypeEnum.FLOAT));
        Assert.assertEquals("INTEGER", TtypeEnum.INTEGER.name());
        Assert.assertEquals("integer", TtypeEnum.INTEGER.value());
        Assert.assertEquals("整数", TtypeEnum.INTEGER.displayName());
        Assert.assertEquals("短整数（10位有效数字）和长整数（19位有效数字）", TtypeEnum.INTEGER.help());
        Assert.assertEquals(1, TtypeEnum.INTEGER.ordinal());
        Assert.assertEquals("", TtypeEnum.INTEGER.toEnumString());
        Assert.assertEquals("", TtypeEnum.INTEGER.toString());
        Assert.assertEquals(-1, TtypeEnum.INTEGER.hashCode());

    }

    @org.junit.Test
    @Order(1)
    @DisplayName("测试多值枚举")
    public void test_测试多值枚举() {
        // 二进制枚举
        List<PersonEnum> personEnums = new ArrayList<>();
        personEnums.add(PersonEnum.O2M);
        Assert.assertTrue(compareEnumList(personEnums, PersonEnum.getEnumsByBits(PersonEnum.class, 4L)));

        //6=2<<1+2<<0
        personEnums.add(PersonEnum.O2O);
        Assert.assertTrue(compareEnumList(personEnums, PersonEnum.getEnumsByBits(PersonEnum.class, 6L)));

        //14=2<<1+2<<0+2<<2
        personEnums.add(PersonEnum.M2O);
        Assert.assertTrue(compareEnumList(personEnums, PersonEnum.getEnumsByBits(PersonEnum.class, 14L)));

        //30=2<<1+2<<0+2<<2+2<<3
        personEnums.add(PersonEnum.M2M);
        Assert.assertTrue(compareEnumList(personEnums, PersonEnum.getEnumsByBits(PersonEnum.class, 30L)));

        // 二进制枚举
        List<String> personNameEnums = new ArrayList<>();
        personNameEnums.add(PersonEnum.O2M.name());
        Assert.assertTrue(compareEnumList(personNameEnums, PersonEnum.getNamesByBits(PersonEnum.class, 4L)));

        //6=2<<1+2<<0
        personNameEnums.add(PersonEnum.O2O.name());
        Assert.assertTrue(compareEnumList(personNameEnums, PersonEnum.getNamesByBits(PersonEnum.class, 6L)));

        //14=2<<1+2<<0+2<<2
        personNameEnums.add(PersonEnum.M2O.name());
        Assert.assertTrue(compareEnumList(personNameEnums, PersonEnum.getNamesByBits(PersonEnum.class, 14L)));

        //30=2<<1+2<<0+2<<2+2<<3
        personNameEnums.add(PersonEnum.M2M.name());
        Assert.assertTrue(compareEnumList(personNameEnums, PersonEnum.getNamesByBits(PersonEnum.class, 30L)));

        // 二进制枚举
        List<Long> personValueEnums = new ArrayList<>();
        personValueEnums.add(new Long(PersonEnum.O2M.value() + ""));
        Assert.assertTrue(compareEnumList(personValueEnums, PersonEnum.getValuesByBits(PersonEnum.class, 4L)));

        //6=2<<1+2<<0
        personValueEnums.add(new Long(PersonEnum.O2O.value() + ""));
        Assert.assertTrue(compareEnumList(personValueEnums, PersonEnum.getValuesByBits(PersonEnum.class, 6L)));

        //14=2<<1+2<<0+2<<2
        personValueEnums.add(new Long(PersonEnum.M2O.value() + ""));
        Assert.assertTrue(compareEnumList(personValueEnums, PersonEnum.getValuesByBits(PersonEnum.class, 14L)));

        //30=2<<1+2<<0+2<<2+2<<3
        personValueEnums.add(new Long(PersonEnum.M2M.value() + ""));
        Assert.assertTrue(compareEnumList(personValueEnums, PersonEnum.getValuesByBits(PersonEnum.class, 30L)));

        // 二进制枚举
        List<String> personDisplayNameEnums = new ArrayList<>();
        personDisplayNameEnums.add(PersonEnum.O2M.displayName());
        Assert.assertTrue(compareEnumList(personDisplayNameEnums, PersonEnum.getDisplayNamesByBits(PersonEnum.class, 4L)));

        //6=2<<1+2<<0
        personDisplayNameEnums.add(PersonEnum.O2O.displayName());
        Assert.assertTrue(compareEnumList(personDisplayNameEnums, PersonEnum.getDisplayNamesByBits(PersonEnum.class, 6L)));

        //14=2<<1+2<<0+2<<2
        personDisplayNameEnums.add(PersonEnum.M2O.displayName());
        Assert.assertTrue(compareEnumList(personDisplayNameEnums, PersonEnum.getDisplayNamesByBits(PersonEnum.class, 14L)));

        //30=2<<1+2<<0+2<<2+2<<3
        personDisplayNameEnums.add(PersonEnum.M2M.displayName());
        Assert.assertTrue(compareEnumList(personDisplayNameEnums, PersonEnum.getDisplayNamesByBits(PersonEnum.class, 30L)));
    }


    @org.junit.Test
    @Order(1)
    @DisplayName("测试枚举的属性")
    public void test_测试枚举的属性() {
        Assert.assertEquals(2, PersonEnum.O2M.getBitPos());
        ;
        Assert.assertTrue(PersonEnum.O2M.isBitIn((long) (2 << 1)));
        Assert.assertFalse(PersonEnum.O2M.isBitIn((long) (2 << 0)));
        Assert.assertEquals((2 << 5 | 2 << 1), PersonEnum.O2M.setBitIn(new Long((2 << 5) + "")));
        Assert.assertEquals((2 << 5), PersonEnum.O2M.unsetBitIn(new Long((2 << 5) + "")));
        ArrayList<PersonEnum> personEnums = new ArrayList<>();
        personEnums.add(PersonEnum.O2M);
        personEnums.add(PersonEnum.M2M);
        personEnums.add(PersonEnum.O2O);
        personEnums.add(PersonEnum.M2O);
        Assert.assertTrue(compareEnumList(personEnums, PersonEnum.getEnumList(PersonEnum.class)));
        Map<String, PersonEnum> enumMap = TtypeEnum.getEnumMap(PersonEnum.class);
        Assert.assertTrue(compareEnumList(personEnums, enumMap.values().stream().collect(Collectors.toList())));

        Assert.assertEquals(PersonEnum.M2O, TtypeEnum.getEnumByValue(PersonEnum.class, PersonEnum.M2O.value()));
        Assert.assertEquals(PersonEnum.O2M, TtypeEnum.getEnum(PersonEnum.class, "O2M"));
        Assert.assertNotEquals(PersonEnum.M2M, TtypeEnum.getEnum(PersonEnum.class, "O2M"));
        Assert.assertEquals(PersonEnum.M2O, TtypeEnum.getEnumByDisplayName(PersonEnum.class, PersonEnum.M2O.displayName()));
        PersonEnum.getEnumsByBits(PersonEnum.class, new Long((2 << 1) + ""));
        Assert.assertEquals(PersonEnum.M2O.displayName(), TtypeEnum.getDisplayNameByName(PersonEnum.class, PersonEnum.M2O.name()));
        Assert.assertEquals(PersonEnum.M2O.displayName(), TtypeEnum.getDisplayNameByValue(PersonEnum.class, PersonEnum.M2O.value()));
        Assert.assertEquals(PersonEnum.M2O.name(), TtypeEnum.getNameByValue(PersonEnum.class, PersonEnum.M2O.value()));
        Assert.assertEquals(PersonEnum.M2O.value(), TtypeEnum.getValueByName(PersonEnum.class, PersonEnum.M2O.name()));

        // 枚举判断与校验
        Assert.assertTrue(TtypeEnum.isHasName(PersonEnum.class, PersonEnum.M2O.name()));
        Assert.assertFalse(TtypeEnum.isHasName(PersonEnum.class, "hhh"));
        Assert.assertTrue(TtypeEnum.isIn(PersonEnum.M2O, (a, b) -> (a.getBitPos() == b.getBitPos()), PersonEnum.M2O));
        Assert.assertFalse(TtypeEnum.isIn(PersonEnum.M2O, (a, b) -> (a.getBitPos() == b.getBitPos()), PersonEnum.M2M));
    }

    @SuppressWarnings({"rawtypes", "ResultOfMethodCallIgnored"})
    @Test
    @Order(1)
    @DisplayName("测试类型工具方法")
    public void testType() {
        for (Field field : TestTypeModel.class.getDeclaredFields()) {
            Class fieldType = field.getType();

            TypeUtils.getType(field);
            TypeUtils.getActualType(field);
            TypeUtils.getActualType(field.getType());

            TypeUtils.getInterfaceGenericType(fieldType);
            TypeUtils.getInterfaceGenericTypes(fieldType);

            TypeUtils.isValidLtype(fieldType.getTypeName());
            TypeUtils.isValidLtypeT(fieldType);
            TypeUtils.isValidLtypeT(fieldType.getTypeName());
            TypeUtils.isValidValueLtype(fieldType.getTypeName());

            TypeUtils.isModelClass(fieldType.getName());
            TypeUtils.isIEnumClass(fieldType);
            TypeUtils.isIEnumClass(fieldType.getName());
            TypeUtils.isValidValueLtype(fieldType.getTypeName());

            TypeUtils.isMap(fieldType);
            TypeUtils.isMap(fieldType.getName());
            TypeUtils.isCollection(fieldType);
            TypeUtils.isCollection(fieldType.getName());
            TypeUtils.isComplexType(fieldType);
            TypeUtils.isComplexType(fieldType.getName());
            TypeUtils.isBaseType(fieldType);
            TypeUtils.isPrimitive(fieldType.getName());

            TypeUtils.getPrimitiveType(fieldType.getTypeName());
        }

    }

    @Test
    @Order(1)
    @DisplayName("测试类名方法")
    public void testClassNameGet() {
        // 数组
        System.out.println(String[].class.getName()); // [Ljava.lang.String;
        System.out.println(String[].class.getCanonicalName()); // java.lang.String[]
        System.out.println(String[].class.getSimpleName()); // String[]
        System.out.println(String[].class.getTypeName()); // java.lang.String[]

        // 成员内部类
        System.out.println(TestTypeModel.TestInner.class.getName()); // lang.reflect.AAA$BBB
        System.out.println(TestTypeModel.TestInner.class.getCanonicalName()); // lang.reflect.AAA.BBB
        System.out.println(TestTypeModel.TestInner.class.getSimpleName()); // BBB
        System.out.println(TestTypeModel.TestInner.class.getTypeName()); // lang.reflect.AAA$BBB

        // 匿名内部类
        System.out.println(new Object() {
        }.getClass().getName()); // lang.reflect.AAA$1
        System.out.println(new Object() {
        }.getClass().getCanonicalName()); // null
        System.out.println(new Object() {
        }.getClass().getSimpleName()); // ""
        System.out.println(new Object() {
        }.getClass().getTypeName()); // lang.reflect.AAA$4

        // 普通类
        System.out.println(TestTypeModel.class.getName()); // lang.reflect.AAA
        System.out.println(TestTypeModel.class.getCanonicalName()); // lang.reflect.AAA
        System.out.println(TestTypeModel.class.getSimpleName()); // AAA
        System.out.println(TestTypeModel.class.getTypeName()); // lang.reflect.AAA

        // 基本数据类型
        System.out.println(int.class.getName()); // int
        System.out.println(int.class.getCanonicalName()); // int
        System.out.println(int.class.getSimpleName()); // int
        System.out.println(int.class.getTypeName()); // int
    }

    Boolean compareEnumList(List list1, List list2) {
        if (list1 == list2) {
            return true;
        }

        // 两个list都为空（包括空指针、元素个数为0）
        if ((list1 == null && list2 != null && list2.size() == 0)
                || (list2 == null && list1 != null && list1.size() == 0)) {
            return true;
        }

        // 两个list元素个数不相同
        if (list1.size() != list2.size()) {
            return false;
        }

        // 两个list元素个数已经相同，再比较两者内容
        // 采用这种可以忽略list中的元素的顺序
        // 涉及到对象的比较是否相同时，确保实现了equals()方法
        if (!list1.containsAll(list2)) {
            return false;
        }

        return true;
    }
}
