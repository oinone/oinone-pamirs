package pro.shushi.pamirs.core.common.test.entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;
import pro.shushi.pamirs.framework.common.entry.TreeNode;

/**
 * {@link TreeNode}测试
 *
 * @author Adamancy Zhang at 16:46 on 2021-09-22
 */
public class TreeNodeTest extends AbstractBaseTest {

    @Test
    public void test() {
        TreeNode<String> root = new TreeNode<>("a", "a1");
        new TreeNode<>("aa", "a11", root);
        new TreeNode<>("ab", "a12", root);
        String jsonString = JSON.toJSONString(root);
        System.out.println(jsonString);
        root = JSON.parseObject(jsonString, new TypeReference<TreeNode<String>>() {
        }.getType());
        System.out.println(1);
    }
}
