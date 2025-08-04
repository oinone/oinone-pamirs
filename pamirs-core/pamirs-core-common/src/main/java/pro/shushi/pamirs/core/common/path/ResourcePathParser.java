package pro.shushi.pamirs.core.common.path;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.Menu;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.boot.web.utils.PageLoadHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * Resource Path Parser
 *
 * @author Adamancy Zhang at 20:41 on 2024-01-04
 */
@Slf4j
@Component
public class ResourcePathParser {

    private static final int VALID_PATH_LENGTH = 2;

    @Resource
    private MetaCacheManager metaCacheManager;

    /**
     * 解析资源路径获取访问信息
     *
     * @param path 资源路径
     * @return 解析结果
     */
    public AccessResourceInfo parseAccessInfo(String path) {
        String[] ps = ResourcePath.PATH_SPLIT_PATTERN.split(path);
        int psl = ps.length;
        if (psl <= VALID_PATH_LENGTH) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid path length. path: {}", path);
            }
            return null;
        }
        AccessResourceInfo info = new AccessResourceInfo(path);
        String currentModel = null;
        StringBuilder pathBuilder = new StringBuilder();
        String firstPath = ps[1];
        String secondPath = ps[2];
        if (resolveModule(info, firstPath)) {
            currentModel = resolveMenu(info, secondPath);
            if (StringUtils.isBlank(currentModel)) {
                currentModel = resolveHomepage(info, secondPath);
            }
        } else if (resolveModel(info, firstPath)) {
            currentModel = resolveAction(info, secondPath);
            info.setIsActionPath(true);
        }
        if (StringUtils.isBlank(currentModel)) {
            return null;
        }
        for (int i = 3; i < psl; i++) {
            String p = ps[i];
            ResourcePath resourcePath = ResourcePathHelper.parsePath(info, currentModel, p);
            String nextModel = convertModel(resourcePath);
            if (nextModel == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid resource path. model: {}, path: {}, p: {}, i: {}", currentModel, path, p, i);
                }
                return null;
            }
            currentModel = nextModel;
            pathBuilder.append(ResourcePath.PATH_SPLIT).append(p);
            info.addPath(resourcePath);
        }
        info.setPath(pathBuilder.toString());
        return info;
    }

    private boolean resolveModule(AccessResourceInfo info, String path) {
        ModuleDefinition moduleDefinition = PamirsSession.getContext().getModule(path);
        if (moduleDefinition == null) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid module definition. module: {}", path);
            }
            return false;
        }
        info.setModule(path);
        info.setModuleDefinition(moduleDefinition);
        return true;
    }

    private boolean resolveModel(AccessResourceInfo info, String path) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(path);
        if (modelConfig == null) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid model definition. model: {}", path);
            }
            return false;
        }
        info.setModel(modelConfig.getModel());
        return true;
    }

    private String resolveMenu(AccessResourceInfo info, String path) {
        String module = info.getModule();
        List<Menu> menus = PageLoadHelper.fetchMenus(module);
        String currentModel = null;
        if (CollectionUtils.isNotEmpty(menus)) {
            for (Menu menu : menus) {
                if (path.equals(menu.getName())) {
                    String model = menu.getModel();
                    String actionName = menu.getActionName();
                    Action action = metaCacheManager.fetchAction(model, actionName);
                    currentModel = getActionModel(action);
                    if (currentModel == null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Invalid action model. model: {}, action: {}", model, actionName);
                        }
                        return null;
                    }
                    info.setMenu(path);
                    info.setModel(model);
                    info.setActionName(actionName);
                    if (action instanceof ViewAction) {
                        info.setViewAction((ViewAction) action);
                    }
                    break;
                }
            }
        }
        return currentModel;
    }

    private String resolveHomepage(AccessResourceInfo info, String path) {
        String currentModel = null;
        ModuleDefinition moduleDefinition = info.getModuleDefinition();
        String homepageModel = moduleDefinition.getHomePageModel();
        String homepageName = moduleDefinition.getHomePageName();
        if (StringUtils.isNoneBlank(homepageModel, homepageName) && path.equals(homepageName)) {
            Action action = metaCacheManager.fetchAction(homepageModel, homepageName);
            currentModel = getActionModel(action);
            if (currentModel == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid action model. model: {}, action: {}", homepageModel, homepageName);
                }
                return null;
            }
            info.setHomepage(path);
            info.setModel(homepageModel);
            info.setActionName(path);
            if (action instanceof ViewAction) {
                info.setViewAction((ViewAction) action);
            }
        } else {
            String[] ps = path.split(ResourcePath.TYPE_SPLIT);
            if (ps.length == 3) {
                ResourcePath resourcePath = ResourcePathHelper.parsePath(info, null, path);
                homepageModel = resourcePath.getModel();
                homepageName = resourcePath.getName();
                Action action = metaCacheManager.fetchAction(homepageModel, homepageName);
                currentModel = getActionModel(action);
                if (currentModel == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Invalid action model. model: {}, action: {}", homepageModel, homepageName);
                    }
                    return null;
                }
                info.setModel(homepageModel);
                info.setActionName(homepageName);
                if (action instanceof ViewAction) {
                    info.setViewAction((ViewAction) action);
                }
            }
        }
        return currentModel;
    }

    private String resolveAction(AccessResourceInfo info, String actionName) {
        String model = info.getModel();
        Action action = metaCacheManager.fetchAction(model, actionName);
        String currentModel = getActionModel(action);
        if (currentModel == null) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid action model. model: {}, action: {}", model, actionName);
            }
            return null;
        }
        info.setActionName(actionName);
        if (action instanceof ViewAction) {
            info.setViewAction((ViewAction) action);
        }
        return currentModel;
    }

    private String convertModel(ResourcePath resourcePath) {
        ResourcePathMetadataType type = resourcePath.getType();
        switch (type) {
            case VIEW:
                return resourcePath.getModel();
            case ACTION:
                return getActionModel(metaCacheManager.fetchAction(resourcePath.getModel(), resourcePath.getName()));
            case FIELD:
                return getFieldModel(PamirsSession.getContext().getModelField(resourcePath.getModel(), resourcePath.getName()));
            default:
                throw new IllegalArgumentException("Invalid resource path metadata type. value = " + type);
        }
    }

    private String getActionModel(Action action) {
        if (action == null) {
            return null;
        }
        String currentModel;
        if (action instanceof ViewAction) {
            ViewAction viewAction = (ViewAction) action;
            currentModel = viewAction.getResModel();
            if (StringUtils.isBlank(currentModel)) {
                currentModel = viewAction.getModel();
            }
        } else {
            currentModel = action.getModel();
        }
        return currentModel;
    }

    private String getFieldModel(ModelFieldConfig modelField) {
        if (modelField == null) {
            return null;
        }
        String ttype = modelField.getTtype();
        if (TtypeEnum.isRelatedType(ttype)) {
            ttype = modelField.getRelatedTtype();
        }
        if (TtypeEnum.isRelationType(ttype)) {
            return modelField.getReferences();
        }
        return modelField.getModel();
    }
}
