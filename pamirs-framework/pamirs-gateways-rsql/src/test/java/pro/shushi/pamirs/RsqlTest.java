package pro.shushi.pamirs;

import cz.jirutka.rsql.parser.RSQLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.gateways.rsql.algorithm.LogicRsqlVisitor;
import pro.shushi.pamirs.framework.gateways.rsql.algorithm.RsqlTreeLeafNode;
import pro.shushi.pamirs.framework.gateways.rsql.algorithm.RsqlTreeLogicalNode;
import pro.shushi.pamirs.framework.gateways.rsql.algorithm.RsqlTreeNode;

import java.util.List;

/**
 * @author shier
 * date  2021/3/25 2:03 下午
 */
public class RsqlTest {

    @Test
    public void test() {
        String rsql = "(A=gt=1 and B=gt=1 and A=lt=1) and A=gt=1 and  A=gt=1 and A=lt=1";
        String rsql2 = "(A=gt=1 and B=gt=1) and A=gt=1 and  A=gt=1 and A=lt=1";
        String rsql3 = "(A=gt=1 and B=gt=1) and A=gt=1";
        RsqlTreeNode accept = analysisRsql(rsql);
        RsqlTreeNode accept2 = analysisRsql(rsql2);
        RsqlTreeNode accept3 = analysisRsql(rsql3);
        Assertions.assertTrue(assertResultSize(accept, 3));
        Assertions.assertTrue(assertResultSize(accept2, 3));
        Assertions.assertTrue(assertResultSize(accept3, 2));
    }

    @Test
    public void test_只有and节点_返回RSQL语句() {
        String rsql = "(A=gt=1 and B=gt=1 and A=lt=1) and A=gt=1 and  A=gt=1 and A=lt=1";
        String rsql2 = "(A=gt=1 and B=gt=1) and A=gt=1 and  A=gt=1 and A=lt=1";
        String rsql3 = "(A=gt=1 and B=gt=1) and A=gt=1";
        String accept = analysisNewRsql(rsql);
        String accept2 = analysisNewRsql(rsql2);
        String accept3 = analysisNewRsql(rsql3);
        Assertions.assertEquals("A=lt='1' and A=gt='1' and B=gt='1'", accept);
        Assertions.assertEquals("A=lt='1' and A=gt='1' and B=gt='1'", accept2);
        Assertions.assertEquals("A=gt='1' and B=gt='1'", accept3);
    }

    @Test
    public void bugTest() {
        //A(B+AC)(B+AC+AD)
        String rsql = "a=gt=1 and (a=gt=2 or (a=gt=1 and a=gt=3)) and (a=gt=2 or (a=gt=1 and a=gt=3) or (a=gt=1 and a=gt=4))";
        String accept = analysisNewRsql(rsql);
        System.out.println(accept);
    }

    @Test
    public void orTest() {
        String rsql = "(A=gt=1 or B=gt=1 or A=lt=1) or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql2 = "(A=gt=1 or B=gt=1) or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql3 = "(A=gt=1 or B=gt=1) or A=gt=1";
        String rsql4 = "A=gt=1 or B=gt=1 or A=lt=1 or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql5 = "A=gt=1 or (B=gt=1 or A=lt=1) or (A=gt=1 or  A=gt=1) or A=lt=1";
        String rsql6 = "(A=gt=1 or B=gt=1 or A=lt=1 or A=gt=1 or  (A=gt=1 or A=lt=1))";
        String rsql7 = "A=gt=1";

        RsqlTreeNode accept4 = analysisRsql(rsql4);
        Assertions.assertTrue(assertResultSize(accept4, 3));
        RsqlTreeNode accept = analysisRsql(rsql);
        RsqlTreeNode accept2 = analysisRsql(rsql2);
        RsqlTreeNode accept3 = analysisRsql(rsql3);
        RsqlTreeNode accept5 = analysisRsql(rsql5);
        RsqlTreeNode accept6 = analysisRsql(rsql6);
        Assertions.assertTrue(assertResultSize(accept, 3));
        Assertions.assertTrue(assertResultSize(accept2, 3));
        Assertions.assertTrue(assertResultSize(accept3, 2));
        Assertions.assertTrue(assertResultSize(accept5, 3));
        Assertions.assertTrue(assertResultSize(accept6, 3));
    }

    @Test
    public void orTest_测试rsql语句的生成为() {
        String rsql = "(A=gt=1 or B=gt=1 or A=lt=1) or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql2 = "(A=gt=1 or B=gt=1) or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql3 = "(A=gt=1 or B=gt=1) or A=gt=1";
        String rsql4 = "A=gt=1 or B=gt=1 or A=lt=1 or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql5 = "A=gt=1 or (B=gt=1 or A=lt=1) or (A=gt=1 or  A=gt=1) or A=lt=1";
        String rsql6 = "(A=gt=1 or B=gt=1 or A=lt=1 or A=gt=1 or  (A=gt=1 or A=lt=1))";
        String rsql7 = "A=gt=1";

        String accept4 = analysisNewRsql(rsql4);
        String accept = analysisNewRsql(rsql);
        String accept2 = analysisNewRsql(rsql2);
        String accept3 = analysisNewRsql(rsql3);
        String accept5 = analysisNewRsql(rsql5);
        String accept6 = analysisNewRsql(rsql6);
        String accept7 = analysisNewRsql(rsql7);
        Assertions.assertEquals("A=lt='1' or A=gt='1' or B=gt='1'", accept4);
        Assertions.assertEquals("A=lt='1' or A=gt='1' or B=gt='1'", accept);
        Assertions.assertEquals("A=lt='1' or A=gt='1' or B=gt='1'", accept2);
        Assertions.assertEquals("A=gt='1' or B=gt='1'", accept3);
        Assertions.assertEquals("A=gt='1'", accept7);
        Assertions.assertEquals("A=lt='1' or A=gt='1' or B=gt='1'", accept5);
        Assertions.assertEquals("A=lt='1' or A=gt='1' or B=gt='1'", accept6);
    }

    @Test
    public void orAndTest() {
        String rsql = "(A=gt=1 and B=gt=1 and A=lt=1) or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql2 = "A=gt=1 and (A=gt=1 and B=gt=1) or A=lt=1";
        String rsql3 = "A=lt=1 and (A=gt=1 and (A=gt=1 and B=gt=1)) or A=lt=1";
        String rsql4 = "A=lt=3 and (A=gt=1 and (A=gt=1 and B=gt=1)) or A=lt=1";
        String rsql5 = "A=lt=1 or (A=gt=1 and (A=gt=1 and B=gt=1) or A=gt=1) or A=lt=1";
        String rsql6 = "A=lt=1 and (A=gt=1 and (A=gt=1 and B=gt=1)) or (A=lt=1 and A=lt=1)";
        String rsql7 = "A=lt=3 and (A=gt=1 or (A=gt=1 and B=gt=1)) or (A=lt=4 and A=lt=1)";
        RsqlTreeNode accept = analysisRsql(rsql);
        RsqlTreeNode accept2 = analysisRsql(rsql2);
        RsqlTreeNode accept3 = analysisRsql(rsql3);
        RsqlTreeNode accept4 = analysisRsql(rsql4);
        RsqlTreeNode accept5 = analysisRsql(rsql5);
        RsqlTreeNode accept6 = analysisRsql(rsql6);
        RsqlTreeNode accept7 = analysisRsql(rsql7);

    }


    @Test
    public void orAndTest_测试SQL() {
        String rsql = "(A=gt=1 and B=gt=1 and A=lt=1) or A=gt=1 or  A=gt=1 or A=lt=1";
        String rsql2 = "A=gt=1 and (A=gt=1 and B=gt=1) or A=lt=1";
        String rsql3 = "A=lt=1 and (A=gt=1 and (A=gt=1 and B=gt=1)) or A=lt=1";
        String rsql4 = "A=lt=3 and (A=gt=1 and (A=gt=1 and B=gt=1)) or A=lt=1";
        String rsql5 = "A=lt=1 or (A=gt=1 and (A=gt=1 and B=gt=1) or A=gt=1) or A=lt=1";
        String rsql6 = "A=lt=1 and (A=gt=1 and (A=gt=1 and B=gt=1)) or (A=lt=1 and A=lt=1)";
        String rsql7 = "A=lt=3 and (A=gt=1 or (A=gt=1 and B=gt=1)) or (A=lt=4 and A=lt=1)";
        String accept = analysisNewRsql(rsql);
        String accept2 = analysisNewRsql(rsql2);
        String accept3 = analysisNewRsql(rsql3);
        String accept4 = analysisNewRsql(rsql4);
        String accept5 = analysisNewRsql(rsql5);
        String accept6 = analysisNewRsql(rsql6);
        String accept7 = analysisNewRsql(rsql7);
        Assertions.assertEquals("A=lt='1' or A=gt='1'", accept);
        Assertions.assertEquals("A=lt='1' or A=gt='1' and B=gt='1'", accept2);
        Assertions.assertEquals("A=lt='1'", accept3);
        Assertions.assertEquals("A=lt='1' or A=lt='3' and A=gt='1' and B=gt='1'", accept4);
        Assertions.assertEquals("A=lt='1' or A=gt='1'", accept5);
        Assertions.assertEquals("A=lt='1'", accept6);
        Assertions.assertEquals("A=lt='3' and A=gt='1' or A=lt='1' and A=lt='4'", accept7);
    }


    private RsqlTreeNode analysisRsql(String rsql) {
        LogicRsqlVisitor logicRsqlVisitor = new LogicRsqlVisitor();
        cz.jirutka.rsql.parser.ast.Node parse = new RSQLParser().parse(rsql);
        RsqlTreeNode accept = parse.accept(logicRsqlVisitor);
        logicRsqlVisitor.optimizeLogicalStatements(accept);
        return accept;
    }

    private String analysisNewRsql(String rsql) {
        LogicRsqlVisitor logicRsqlVisitor = new LogicRsqlVisitor();
        cz.jirutka.rsql.parser.ast.Node parse = new RSQLParser().parse(rsql);
        RsqlTreeNode accept = parse.accept(logicRsqlVisitor);
        return logicRsqlVisitor.optimizeLogicalStatementRsql(accept);
    }

    Boolean assertResultSize(RsqlTreeNode node, int size) {

        if (node instanceof RsqlTreeLeafNode) {
            return ((RsqlTreeLeafNode) node).getQueries().size() == size;
        } else if (node instanceof RsqlTreeLogicalNode) {
            List<RsqlTreeNode> childrenNodes = ((RsqlTreeLogicalNode) node).getChildrenNodes();
            return childrenNodes.size() == size;
        } else if (node instanceof RsqlTreeNode) {
            List<RsqlTreeNode> childrenNodes = node.getChildrenNodes();
            if (childrenNodes.size() > 1) {
                return childrenNodes.size() == size;
            } else if (childrenNodes.size() == 1) {
                RsqlTreeNode virtualRoot = childrenNodes.get(0);
                if (virtualRoot instanceof RsqlTreeLeafNode) {
                    return ((RsqlTreeLeafNode) virtualRoot).getQueries().size() == size;
                } else {
                    return assertResultSize(childrenNodes.get(0), size);
                }
            }
        }
        return Boolean.FALSE;
    }


    @Test
    public void test_ti() {
        String[] strings = {"A+A+B*C",
                "A+(A+B*C)",
                "A+A+A+B*C",
                "A+((A+A))+B*C",
                "A+A+B*C+B*C",
                "A+A+A+B*C+B*C+B*C",
                "A+A+A+B*C+B*C",
                "A+A*(B+C)",
                "A+(B+C)*A+B*A+C*A",
                "A+(B+C)*A+B*C*D+B*D",
                "A+(B+C)*A+B*A+C*A+D*E+A*D*E+B*D*E",
                "A+(B+C)*A+B*A+C*A+B*C*D+B*D",
                "A+B*(A+C)",
                "A+B*(A+C)+B*(A+C)",
                "A+B*(A+C)+D*E+A*D*E",
                "A+B*(A+C)+B*(A+C)+D*E+A*D*E+B*D*E",
                "A+B*(A+C)+B*(A+C)+D*E+A*E*D",
                "A*A*(B+C)",
                "A*A*A*(B+C)",
                "A*A*(B+C)*(B+C)",
                "A*A*A*(B+C)*(B+C)*(B+C)",
                "A*A*A*(B+C)*(B+C)",
                "A*(B*C+A)",
                "A*(B*C+A)*(B*C+A)",
                "A*(B*C+A)*E*(D*F+E)",
                "A*(B*C+A)*(B*C+A)*E*(D*F+E)*(C*F+E)",
                "A*(B*C+A)*(B*C+A)*E*(D*F+E)",
                "A*(B+A*C)",
                "A*(B+A*C)*(B+A*C)",
                "A*(B+A*C)*(B+A*C+A*D)",
                "A*(B+A*C)*E*(D*E+F)",
                "(B+A*C)*A*(D*E+F)*E",
                "A*(B+A*C)*(B+A*C)*E*(D*E+F)*(D*E+F)",
                "A*(B+A*C)*(B+A*C+A*D)*E*(D*E+F)*(D*E+F)",
                "A*(B+A*C)*(B+A*C)*E*(D*E+F)",
                "A*(B+A*C)*(B+A*C+A*D)*E*(D*E+F)",
                "A+A*B+B*(A*A+C)",
                "A+B+C+D+E+F+G",
                "A*B*C*D*E*F*G*H",
                "A*(B+C)",
                "A+((B+C)*A+B*C*D)+B*D",
                "A+(B+C)*A+(B*C*D+B*D)",
                "A",
                "A*B+B*A+C*D*B+B*D*C"};
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].replace("+", " or ");
            strings[i] = strings[i].replace("*", " and ");
            strings[i] = strings[i].replace("A", "a=gt=1");
            strings[i] = strings[i].replace("B", "a=gt=2");
            strings[i] = strings[i].replace("C", "a=gt=3");
            strings[i] = strings[i].replace("D", "a=gt=4");
            strings[i] = strings[i].replace("E", "a=gt=5");
            strings[i] = strings[i].replace("F", "a=gt=6");
            strings[i] = strings[i].replace("G", "a=gt=7");
            strings[i] = strings[i].replace("H", "a=gt=8");
            String result = analysisNewRsql(strings[i]);
            result = result.replace("a=gt='1'", "A");
            result = result.replace("a=gt='2'", "B");
            result = result.replace("a=gt='3'", "C");
            result = result.replace("a=gt='4'", "D");
            result = result.replace("a=gt='5'", "E");
            result = result.replace("a=gt='6'", "F");
            result = result.replace("a=gt='7'", "G");
            result = result.replace("a=gt='8'", "H");
            result = result.replace(" and ", "");
            result = result.replace(" or ", "+");

            System.out.println(result);
        }
    }
}
