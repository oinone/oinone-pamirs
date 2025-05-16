package pro.shushi.pamirs.eip.core.init;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;

import java.util.Collections;
import java.util.List;

/**
 * EipDataInit
 *
 * @author yakir on 2023/03/30 10:46.
 */
@Component
public class EipDataInit implements InstallDataInit{

    private void initConnGroup() {
        // 只在安装时初始化一次,允许用户删除
        new EipConnGroup().createOrUpdateBatch(
                Lists.newArrayList(
                        new EipConnGroup().setName("OA"),
                        new EipConnGroup().setName("ERP"),
                        new EipConnGroup().setName("物流仓储"),
                        new EipConnGroup().setName("生产制造"),
                        new EipConnGroup().setName("商业交易"),
                        new EipConnGroup().setName("工具"),
                        new EipConnGroup().setName("其他")
                )
        );
    }

    @Override
    @Transactional
    public boolean init(AppLifecycleCommand command, String version) {
        initConnGroup();

        return true;
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(EipModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}
