package pro.shushi.pamirs.auth.api.utils;

import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

/**
 * 资源编码生成器
 *
 * @author Adamancy Zhang at 21:08 on 2024-08-22
 */
public class AuthResourceCodeGenerator {

    private AuthResourceCodeGenerator() {
        // reject create object
    }

    public static String generatorModuleResourceCode(ModuleDefinition module) {
        return module.getModule();
    }

    public static String generatorHomepageResourceCode(ModuleDefinition module, ViewAction action) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                module.getModule(),
                action.getModel(),
                action.getName()
        );
    }

    public static String generatorMenuResourceCode(Menu menu) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                menu.getModule(),
                menu.getName()
        );
    }

    public static String generatorServerActionResourceCode(ServerAction action) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                ResourcePermissionSubtypeEnum.SERVER_ACTION.value(),
                action.getModel(),
                action.getName()
        );
    }

    public static String generatorViewActionResourceCode(ViewAction action) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                ResourcePermissionSubtypeEnum.VIEW_ACTION.value(),
                action.getModel(),
                action.getName()
        );
    }

    public static String generatorUrlActionResourceCode(UrlAction action) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                ResourcePermissionSubtypeEnum.URL_ACTION.value(),
                action.getModel(),
                action.getName()
        );
    }

    public static String generatorClientActionResourceCode(ClientAction action) {
        return StringHelper.join(CharacterConstants.SEPARATOR_OCTOTHORPE,
                ResourcePermissionSubtypeEnum.CLIENT_ACTION.value(),
                action.getModel(),
                action.getName()
        );
    }
}
