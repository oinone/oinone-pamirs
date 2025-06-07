package pro.shushi.pamirs.trigger.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.ExtendBuildInit;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

/**
 * @author Adamancy Zhang
 * @date 2020-11-09 16:13
 */
@Component
public class ScheduleExtendBuildComputer implements ExtendBuildInit {

    @Override
    public void build(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap) {
//        System.out.println(1);
    }

    @Override
    public int priority() {
        return 99;
    }

}
