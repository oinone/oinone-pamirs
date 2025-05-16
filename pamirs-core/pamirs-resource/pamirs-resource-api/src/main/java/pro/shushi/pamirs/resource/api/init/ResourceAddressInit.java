package pro.shushi.pamirs.resource.api.init;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.resource.api.ResourceModule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: Wuer
 * @email: syj@shushi.pro
 * @Date: 2020/4/29 2:09 下午
 * @Description: 初始化地址库
 */
@Component
public class ResourceAddressInit implements MetaDataEditor, InstallDataInit, UpgradeDataInit {


    @Override
    public List<String> modules() {
        return Collections.singletonList(ResourceModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        initResourceAddress();
    }

    @Override
    public boolean init(AppLifecycleCommand command, String version) {
        initResourceAddress();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initResourceAddress();
        return true;
    }

    private void initResourceAddress() {
//        if (!Boolean.TRUE.equals(isInitAddress)) {
//            return;
//        }
//        //初始化国家、省、市、区 和 对应的ResourceRegion
//


    }
}
