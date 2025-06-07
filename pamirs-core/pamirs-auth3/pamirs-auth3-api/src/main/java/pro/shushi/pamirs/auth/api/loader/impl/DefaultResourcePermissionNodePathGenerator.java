package pro.shushi.pamirs.auth.api.loader.impl;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.entity.node.ActionPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.HomepagePermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.MenuPermissionNode;
import pro.shushi.pamirs.auth.api.entity.node.ModulePermissionNode;
import pro.shushi.pamirs.auth.api.helper.AuthHelper;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodePathGenerator;
import pro.shushi.pamirs.auth.api.loader.visitor.AuthCompileContext;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;

/**
 * 默认资源权限节点路径生成器
 *
 * @author Adamancy Zhang at 12:55 on 2024-02-04
 */
@Component(AuthConstants.RESOURCE_PERMISSION_PATH_GENERATOR_BEAN_NAME)
public class DefaultResourcePermissionNodePathGenerator implements ResourcePermissionNodePathGenerator {

    @Override
    public String generatorModulePath(ModulePermissionNode moduleNode) {
        String module = moduleNode.getModule();
        if (AuthHelper.isModuleInWhite(module)) {
            return ResourcePath.generatorPath(module, AuthConstants.ALL_FLAG_STRING);
        }
        return ResourcePath.generatorPath(module);
    }

    @Override
    public String generatorHomepagePath(HomepagePermissionNode homepageNode) {
        return ResourcePath.generatorPath(homepageNode.getModule(), AuthConstants.HOMEPAGE_TYPE);
    }

    @Override
    public String generatorMenuPath(MenuPermissionNode menuNode) {
        return ResourcePath.PATH_SPLIT + menuNode.getName();
    }

    @Override
    public String generatorMenuPath(ModulePermissionNode moduleNode, MenuPermissionNode menuNode) {
        return moduleNode.getPath() + menuNode.getPath();
    }

    @Override
    public String generatorMenuPath(ModulePermissionNode moduleNode, MenuPermissionNode menuNode, MenuPermissionNode parentMenuNode) {
        return moduleNode.getPath() + menuNode.getPath();
    }

    @Override
    public String generatorActionPath(AuthCompileContext context, ActionPermissionNode actionNode) {
        AccessResourceInfo info = context.getInfo().clone();
        info.addActionPath(actionNode.getModel(), actionNode.getAction());
        return info.toString();
    }

    @Override
    public String generatorAllActionPath(AuthCompileContext context, ActionPermissionNode actionNode) {
        return context.getInfo().toString() + AuthConstants.ALL_FLAG_PATH_SUFFIX;
    }
}
