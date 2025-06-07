package pro.shushi.pamirs.core.common.test.xstream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.xstream.TreeNodeXStream;
import pro.shushi.pamirs.core.common.xstream.XMLNodeContent;
import pro.shushi.pamirs.framework.common.entry.TreeNode;

/**
 * {@link TreeNodeXStream}测试
 *
 * @author Adamancy Zhang at 14:05 on 2021-08-23
 */
@DisplayName("TreeNodeXStream测试")
public class TreeNodeXStreamTest {

    private static final String xml = "<view widget=\"form\" model=\"workflow.TestWorkflow\" viewName=\"工作流演示模型form\" title=\"工作流演示模型\">\n" +
            "    <field name=\"id\" invisible=\"true\"/>\n" +
            "    <field name=\"name\" title=\"名称\"/>\n" +
            "    <field name=\"fieldStr\" title=\"文本\"/>\n" +
            "    <field name=\"fieldInt\" title=\"整数\"/>\n" +
            "</view>";

    @Test
    public void test() {
        TreeNodeXStream xStream = new TreeNodeXStream();
        TreeNode<XMLNodeContent> root = xStream.fromXML(xml);
        String s = xStream.toXML(root);
        System.out.println(1);
    }
}
