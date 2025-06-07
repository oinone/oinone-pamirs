package pro.shushi.pamirs.framework.faas.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.framework.faas.script.ScriptRunner;

import java.util.List;

/**
 * 表达式测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("表达式测试")
public class ScriptTest {

    @Test
    @Order(0)
    @DisplayName("测试groovy表达式功能")
    public void testGroovyExpression() {
        List<Object> result = ScriptRunner.run("1..3");
        AssertionErrors.assertEquals("range数组转化", 3, result.size());
        result = ScriptRunner.run("[1,2,3]");
        AssertionErrors.assertEquals("数组转化", 3, result.size());
    }


//    public static void main(String[] args){
//        // EL本地调用-表达式
//        Object[] args1 = new Object[]{null, null};
//        System.out.println("mvel call1:" + Fun.get().run(CallType.EL.getType(), "a == empty && b == empty", new String[]{"a","b"}, args1));
//
//        // EL本地调用-方法
//        Object[] args2 = new Object[]{1, 2};
//        System.out.println("mvel call2:" + Fun.get().run(CallType.EL.getType(), "num1 + num2", new String[]{"num1","num2"}, args2));
//    }


//    public static void main(String[] args){
//        // groovy本地调用-表达式
//        Object[] args1 = new Object[]{null, null};
//        System.out.println("groovy call:" + Fun.get().run(new Functions().setType(CallType.GROOVY.getType()).setArgTypes("java.lang.String, java.lang.String").setArgNames("a,b").setCodes("return a == null && b == null;"), args1));
//
//        Object[] args2 = new Object[]{1, 2};
//        System.out.println("groovy call:" + Fun.get().run(new Functions().setType(CallType.GROOVY.getType()).setArgNames("a,b").setCodes("return a + b;"), args2));
//
//        Object[] args3 = new String[]{"asdfds", "adfsdf"};
//        System.out.println("groovy call:" + Fun.get().run(new Functions().setType(CallType.GROOVY.getType()).setArgTypes("java.lang.String").setArgNames("content").setCodes("pro.shushi.pamirs.base.faas.builtin.BuiltInFunctions.getBuiltIn().trim(content);"), "ssfdfd"));
//    }

}
