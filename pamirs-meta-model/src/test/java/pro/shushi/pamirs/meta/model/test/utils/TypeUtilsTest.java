package pro.shushi.pamirs.meta.model.test.utils;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.enumclass.RtypeEnumCls;
import pro.shushi.pamirs.meta.enumclass.TtypeEnumCls;
import pro.shushi.pamirs.meta.model.test.AbstractBaseTest;
import pro.shushi.pamirs.meta.model.test.model.TestTypeModel;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;

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
    @Order(1)
    @DisplayName("枚举测试")
    public void testEnum() {
        TtypeEnumCls[] tenums = TtypeEnumCls.INTEGER.values();
        RtypeEnumCls[] renums = RtypeEnumCls.O2O.values();
        TtypeEnumCls[] ptenums = TtypeEnumCls.values();
        RtypeEnumCls[] prenums = RtypeEnumCls.values();
        Assert.assertEquals(TtypeEnumCls.INTEGER, tenums[0]);
        Assert.assertEquals(RtypeEnumCls.O2O, renums[0]);
        Assert.assertEquals(TtypeEnumCls.INTEGER, ptenums[0]);
        Assert.assertEquals(RtypeEnumCls.O2O, prenums[0]);
        Assert.assertEquals(TtypeEnumCls.INTEGER, TtypeEnumCls.INTEGER.valueOf(TtypeEnumCls.INTEGER.name()));
        Assert.assertEquals(TtypeEnumCls.O2O, TtypeEnumCls.O2O.valueOf(RtypeEnumCls.O2O.name()));
        Assert.assertEquals(TtypeEnumCls.INTEGER, TtypeEnumCls.valueOf(TtypeEnumCls.INTEGER.name()));
        Assert.assertEquals(TtypeEnumCls.O2O, TtypeEnumCls.valueOf(RtypeEnumCls.O2O.name()));
    }

    @Test
    @Order(1)
    @DisplayName("测试类型工具方法")
    public void testType() throws ClassNotFoundException {
        for(Field field : TestTypeModel.class.getDeclaredFields()){
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
            TypeUtils.isEnumClass(fieldType);
            TypeUtils.isEnumClass(fieldType.getName());
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
    public void testClassNameGet(){
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
        System.out.println(new Object(){}.getClass().getName()); // lang.reflect.AAA$1
        System.out.println(new Object(){}.getClass().getCanonicalName()); // null
        System.out.println(new Object(){}.getClass().getSimpleName()); // ""
        System.out.println(new Object(){}.getClass().getTypeName()); // lang.reflect.AAA$4

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

}
