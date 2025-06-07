package pro.shushi.pamirs.file.test;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.common.entry.TreeNode;

/**
 * @author Adamancy Zhang on 2021-04-19 14:19
 */
public class TreeNodeTest {

    @Test
    public void test() {
        TreeNode<Integer> root = new TreeNode<>("1", 1);
        TreeNode<Integer> child = new TreeNode<>("2.1", 2, root);
        new TreeNode<>("2.2", 2, root);
        new TreeNode<>("2.3", 2, root);
        new TreeNode<>("3.1", 3, child);
        new TreeNode<>("3.2", 3, child);
        System.out.println(JSON.toJSONString(root));
    }
}
