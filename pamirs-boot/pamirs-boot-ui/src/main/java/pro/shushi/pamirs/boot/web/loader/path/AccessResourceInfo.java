package pro.shushi.pamirs.boot.web.loader.path;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问资源信息
 *
 * @author Adamancy Zhang at 21:33 on 2024-01-04
 */
public class AccessResourceInfo {

    private static final int PATHS_INITIAL_CAPACITY = 8;

    private final String originPath;

    private String module;

    private ModuleDefinition moduleDefinition;

    private String model;

    private String homepage;

    private String menu;

    private String actionName;

    private ViewAction viewAction;

    private String path;

    private List<ResourcePath> paths;

    private AuthorizationPath authorizationPath;

    /**
     * 当该属性为true时表示以下指定属性为函数相关属性，并非动作属性
     * <p>
     * model -> namespace
     * actionName -> fun
     * </p>
     */
    private boolean isFunction;

    /**
     * 当该属性为true时表示会话路径以<code>/{model}/{actionName}</code>作为前缀，且不包含模块
     */
    private boolean isActionPath;

    /**
     * 是否为经过修复的访问信息
     */
    private boolean isFixed;

    public AccessResourceInfo() {
        this(null);
    }

    public AccessResourceInfo(String originPath) {
        this.originPath = originPath;
        this.paths = new ArrayList<>(PATHS_INITIAL_CAPACITY);
    }

    public String getOriginPath() {
        return originPath;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public ModuleDefinition getModuleDefinition() {
        return moduleDefinition;
    }

    public void setModuleDefinition(ModuleDefinition moduleDefinition) {
        this.moduleDefinition = moduleDefinition;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public ViewAction getViewAction() {
        return viewAction;
    }

    public void setViewAction(ViewAction viewAction) {
        this.viewAction = viewAction;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ResourcePath> getPaths() {
        return paths;
    }

    public void setPaths(List<ResourcePath> paths) {
        this.paths = paths;
    }

    public void addPath(ResourcePath path) {
        this.paths.add(path);
    }

    public void addViewPath(String model, String name) {
        this.addPath(this.generatorActionPath(model, name));
    }

    public ResourcePath generatorViewPath(String model, String name) {
        return generatorPath(ResourcePathMetadataType.VIEW, model, name);
    }

    public void addFieldPath(String model, String name) {
        this.addPath(this.generatorFieldPath(model, name));
    }

    public ResourcePath generatorFieldPath(String model, String name) {
        return generatorPath(ResourcePathMetadataType.FIELD, model, name);
    }

    public void addActionPath(String model, String name) {
        this.addPath(this.generatorActionPath(model, name));
    }

    public ResourcePath generatorActionPath(String model, String name) {
        return generatorPath(ResourcePathMetadataType.ACTION, model, name);
    }

    private ResourcePath generatorPath(ResourcePathMetadataType type, String model, String name) {
        ResourcePath lastPath = this.getLastPath();
        ResourcePath path;
        if (lastPath == null) {
            path = new ResourcePath(type, model, name);
        } else {
            path = new ResourcePath(type, model, name, model.equals(lastPath.getModel()));
        }
        return path;
    }

    public ResourcePath getLastPath() {
        int size = this.paths.size();
        if (size >= 1) {
            return this.paths.get(size - 1);
        }
        return null;
    }

    public AuthorizationPath getAuthorizationPath() {
        return authorizationPath;
    }

    public void setAuthorizationPath(AuthorizationPath authorizationPath) {
        this.authorizationPath = authorizationPath;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public void setIsFunction(boolean isFunction) {
        this.isFunction = isFunction;
    }

    public boolean isActionPath() {
        return isActionPath;
    }

    public void setIsActionPath(boolean isActionPath) {
        this.isActionPath = isActionPath;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public String getAccessModel() {
        List<ResourcePath> paths = this.getPaths();
        if (CollectionUtils.isEmpty(paths)) {
            return this.getModel();
        }
        return paths.get(paths.size() - 1).getModel();
    }

    public String getSessionPath() {
        StringBuilder builder = getSessionPathBuilder();
        if (builder == null) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = getSessionPathBuilder();
        if (builder == null) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        for (ResourcePath path : this.getPaths()) {
            builder.append(ResourcePath.PATH_SPLIT).append(path.toString());
        }
        return builder.toString();
    }

    private StringBuilder getSessionPathBuilder() {
        String module = getModule();
        StringBuilder builder;
        if (StringUtils.isBlank(module)) {
            builder = new StringBuilder();
        } else {
            builder = new StringBuilder(ResourcePath.PATH_SPLIT + module);
        }
        String homepage = getHomepage();
        String menu = getMenu();
        if (StringUtils.isNotBlank(menu)) {
            builder.append(ResourcePath.PATH_SPLIT).append(menu);
        } else if (StringUtils.isNotBlank(homepage)) {
            builder.append(ResourcePath.PATH_SPLIT).append(homepage);
        } else {
            String model = getModel();
            String actionName = getActionName();
            if (StringUtils.isNoneBlank(model, actionName)) {
                builder = new StringBuilder(ResourcePath.PATH_SPLIT + model + ResourcePath.PATH_SPLIT + actionName);
            }
        }
        return builder;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public AccessResourceInfo clone() {
        AccessResourceInfo info = new AccessResourceInfo(this.originPath);
        info = this.transfer(info);
        info.paths = this.paths.stream().map(ResourcePath::clone).collect(Collectors.toList());
        return info;
    }

    public <R extends AccessResourceInfo> R transfer(R target) {
        target.setModule(this.getModule());
        target.setModuleDefinition(this.getModuleDefinition());
        target.setModel(this.getModel());
        target.setHomepage(this.getHomepage());
        target.setMenu(this.getMenu());
        target.setActionName(this.getActionName());
        target.setViewAction(this.getViewAction());
        target.setPath(this.getPath());
        target.setIsFunction(this.isFunction());
        target.setIsFixed(this.isFixed());
        return target;
    }
}
