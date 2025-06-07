package pro.shushi.pamirs.auth.view.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.model.permission.AuthResourcePermission;
import pro.shushi.pamirs.auth.api.service.permission.AuthResourcePermissionService;
import pro.shushi.pamirs.auth.api.utils.AuthPermissionGenerator;
import pro.shushi.pamirs.boot.base.model.*;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 权限资源节点操作工具类
 *
 * @author Adamancy Zhang at 18:23 on 2024-01-19
 */
@Component
public class AuthResourceNodeOperator {

    @Autowired
    private AuthResourcePermissionService authResourcePermissionService;

    public <T extends ResourceNodeEntity, R> R verificationResourceNode(T data,
                                                                        Function<UeModule, R> moduleConsumer,
                                                                        BiFunction<UeModule, ViewAction, R> homepageConsumer,
                                                                        Function<Menu, R> menuConsumer,
                                                                        Function<ServerAction, R> serverActionConsumer,
                                                                        Function<ViewAction, R> viewActionConsumer,
                                                                        Function<UrlAction, R> urlActionConsumer,
                                                                        Function<ClientAction, R> clientActionConsumer) {
        PermissionMateDataEnum nodeType = data.getNodeType();
        if (nodeType == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
        }
        Long resourceId = data.getResourceId();
        if (resourceId == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ID_ERROR).errThrow();
        }
        String path = data.getPath();
        if (StringUtils.isBlank(path)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_PATH_ERROR).errThrow();
        }
        switch (nodeType) {
            case MODULE:
                return moduleConsumer.apply(queryResourceById(UeModule.MODEL_MODEL, resourceId));
            case HOMEPAGE: {
                UeModule module = queryResourceById(UeModule.MODEL_MODEL, resourceId);
                String homepageModel = module.getHomePageModel();
                String homepageActionName = module.getHomePageName();
                if (StringUtils.isAnyBlank(homepageModel, homepageActionName)) {
                    return null;
                }
                return homepageConsumer.apply(module, queryHomepageAction(homepageModel, homepageActionName));
            }
            case MENU:
                return menuConsumer.apply(queryResourceById(Menu.MODEL_MODEL, resourceId));
            case SERVER_ACTION:
                return serverActionConsumer.apply(queryResourceById(ServerAction.MODEL_MODEL, resourceId));
            case VIEW_ACTION:
                return viewActionConsumer.apply(queryResourceById(ViewAction.MODEL_MODEL, resourceId));
            case URL_ACTION:
                return urlActionConsumer.apply(queryResourceById(UrlAction.MODEL_MODEL, resourceId));
            case CLIENT_ACTION:
                return clientActionConsumer.apply(queryResourceById(ClientAction.MODEL_MODEL, resourceId));
            default:
                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
        }
    }

    private ViewAction queryHomepageAction(String homepageModel, String homepageActionName) {
        ViewAction action = Models.origin().queryOneByWrapper(Pops.<ViewAction>lambdaQuery()
                .from(ViewAction.MODEL_MODEL)
                .eq(ViewAction::getModel, homepageModel)
                .eq(ViewAction::getName, homepageActionName));
        if (action == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        return action;
    }

    private <T extends IdModel> T queryResourceById(String modelModel, Long resourceId) {
        T action = Models.origin().queryOneByWrapper(Pops.<T>lambdaQuery()
                .from(modelModel)
                .eq(IdModel::getId, resourceId));
        if (action == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
        }
        return action;
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateModulePermission(T data, UeModule module) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorModulePermission(module, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateHomepagePermission(T data, UeModule module, ViewAction action) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorHomepagePermission(module, action, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateMenuPermission(T data, Menu menu) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorMenuPermission(menu, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateServerActionPermission(T data, ServerAction action) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorServerActionPermission(action, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateViewActionPermission(T data, ViewAction action) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorViewActionPermission(action, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateUrlActionPermission(T data, UrlAction action) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorUrlActionPermission(action, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }

    public <T extends ResourceNodeEntity> AuthResourcePermission createOrUpdateClientActionPermission(T data, ClientAction action) {
        AuthResourcePermission permission = AuthPermissionGenerator.generatorClientActionPermission(action, data.getPath());
        return authResourcePermissionService.createOrUpdate(permission);
    }
}
