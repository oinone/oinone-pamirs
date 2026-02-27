package pro.shushi.pamirs.apps.api.service;

import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;

public interface AppsManagementModuleService {

    AppsManagementModule bindHomePage(AppsManagementModule data);

    AppsManagementModule install(AppsManagementModule module);

    AppsManagementModule upgrade(AppsManagementModule module);

    AppsManagementModule reload(AppsManagementModule module);

    AppsManagementModule uninstall(AppsManagementModule module);

}
