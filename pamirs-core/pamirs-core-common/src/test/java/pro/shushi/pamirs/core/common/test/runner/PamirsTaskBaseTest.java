package pro.shushi.pamirs.core.common.test.runner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.task.PamirsChainTask;
import pro.shushi.pamirs.core.common.task.PamirsRelationTask;
import pro.shushi.pamirs.core.common.task.PamirsTask;
import pro.shushi.pamirs.core.common.task.extension.DefaultChainPamirsTask;
import pro.shushi.pamirs.core.common.task.extension.DefaultPamirsRelationTask;
import pro.shushi.pamirs.core.common.task.extension.DefaultPamirsTask;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;

/**
 * @author Adamancy Zhang on 2021-05-09 16:51
 */
@DisplayName("任务基础功能实现测试")
public class PamirsTaskBaseTest extends AbstractBaseTest {

    @DisplayName("构造功能测试-标准任务")
    @Test
    public void test1() {
        PamirsTask target = new DefaultPamirsTask("task target");
        PamirsRelationTask relationTask = new DefaultPamirsRelationTask("relation task");
        PamirsChainTask chainTask = new DefaultChainPamirsTask("chain task");
        relationTask.setParent(target);
        relationTask.addChild(target);
        chainTask.setPrevious(target);
        chainTask.setNext(target);
        System.out.println("断点测试成功");
    }

    @DisplayName("构造功能测试-关联任务")
    @Test
    public void test2() {
        PamirsRelationTask target = new DefaultPamirsRelationTask("relation task target");
        PamirsRelationTask relationTask = new DefaultPamirsRelationTask("relation task");
        PamirsChainTask chainTask = new DefaultChainPamirsTask("chain task");
        relationTask.setParent(target);
        relationTask.addChild(target);
        chainTask.setPrevious(target);
        chainTask.setNext(target);
        System.out.println("断点测试成功");
    }

    @DisplayName("构造功能测试-链式任务")
    @Test
    public void test3() {
        PamirsChainTask target = new DefaultChainPamirsTask("chain task target");
        PamirsRelationTask relationTask = new DefaultPamirsRelationTask("relation task");
        PamirsChainTask chainTask = new DefaultChainPamirsTask("chain task");
        relationTask.setParent(target);
        relationTask.addChild(target);
        chainTask.setPrevious(target);
        chainTask.setNext(target);
        System.out.println("断点测试成功");
    }
}
