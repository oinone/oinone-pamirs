package pro.shushi.pamirs.apps.api.service;

import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;

import java.util.regex.Pattern;

public interface AppsManagementModuleService {

    Pattern moduleModulePattern = Pattern.compile("^[a-zA-Z](?!.*__.*)[a-zA-Z0-9_]+[a-zA-Z0-9]$");
    Pattern moduleAbbreviatePattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9]$");

    AppsManagementModule create(AppsManagementModule data);

    AppsManagementModule update(AppsManagementModule data);

    AppsManagementModule bindHomePage(AppsManagementModule data);

    AppsManagementModule install(AppsManagementModule module);

    AppsManagementModule upgrade(AppsManagementModule module);

    AppsManagementModule reload(AppsManagementModule module);

    AppsManagementModule uninstall(AppsManagementModule module);
}
