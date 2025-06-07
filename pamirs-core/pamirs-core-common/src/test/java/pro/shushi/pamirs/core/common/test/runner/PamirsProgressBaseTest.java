package pro.shushi.pamirs.core.common.test.runner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.progress.PamirsProgress;
import pro.shushi.pamirs.core.common.progress.extension.DefaultPamirsProgress;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;

/**
 * @author Adamancy Zhang on 2021-05-09 15:13
 */
@DisplayName("进度基础功能测试")
public class PamirsProgressBaseTest extends AbstractBaseTest {

    @DisplayName("正常操作测试-set")
    @Test
    public void setTest() {
        PamirsProgress progress = new DefaultPamirsProgress();

        assert !progress.isStarted();
        assert !progress.isFinished();
        assert !progress.isRunning();
        assert progress.getStartTime() == -1;
        assert progress.getEndTime() == -1;
        assert progress.get() == 0;
        progress.start();
        assert progress.isStarted();
        assert !progress.isFinished();
        assert progress.isRunning();
        assert progress.getStartTime() != -1;
        assert progress.getEndTime() == -1;
        assert progress.get() == 0;

        progress.set(1);
        assert progress.get() == 1;

        progress.set(2);
        assert progress.get() == 2;

        progress.set(98);
        assert progress.get() == 98;

        progress.set(99);
        assert progress.get() == 99;

        assert progress.isStarted();
        assert !progress.isFinished();
        assert progress.isRunning();
        assert progress.getStartTime() != -1;
        assert progress.getEndTime() == -1;
        progress.finish();
        assert progress.isStarted();
        assert progress.isFinished();
        assert !progress.isRunning();
        assert progress.getStartTime() != -1;
        assert progress.getEndTime() != -1;
        assert progress.get() == 100;
    }

    @DisplayName("正常操作测试-increment")
    @Test
    public void incrementTest() {
        PamirsProgress progress = new DefaultPamirsProgress();
        progress.start();

        progress.increment(1);
        assert progress.get() == 1;

        progress.increment(1);
        assert progress.get() == 2;
        progress.finish();

        progress = new DefaultPamirsProgress();
        progress.start();
        progress.increment(99);
        assert progress.get() == 99;
        progress.finish();
    }
}
