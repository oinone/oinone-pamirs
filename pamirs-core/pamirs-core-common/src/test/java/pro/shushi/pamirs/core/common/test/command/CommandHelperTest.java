package pro.shushi.pamirs.core.common.test.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.core.common.command.CommandHelper;
import pro.shushi.pamirs.core.common.test.AbstractBaseTest;

import java.io.IOException;

/**
 * @author Adamancy Zhang on 2021-05-20 10:07
 */
@DisplayName("命令行帮助类测试")
public class CommandHelperTest extends AbstractBaseTest {

    @DisplayName("ls")
    @Test
    public void lsTest() throws IOException, InterruptedException {
        CommandHelper.executeCommands(CommandHelper.BASH, "ls");
    }
}
