package pro.shushi.pamirs.eip.core.init;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.locale.utils.I18nUtils;

import java.util.Collections;
import java.util.List;

/**
 * EipDataInit
 *
 * @author yakir on 2023/03/30 10:46.
 */
@Component
public class EipDataInit implements InstallDataInit {

    private void initConnGroup() {
        // Initialize only once during installation, allowing user deletion
        new EipConnGroup().createOrUpdateBatch(
                Lists.newArrayList(
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.OA")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.ERP")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Logistics")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Manufacturing")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Commerce")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Tools")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Others"))
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
