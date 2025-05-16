package pro.shushi.pamirs.auth.api.utils;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.core.common.path.ResourcePathHelper;

import javax.annotation.Resource;

/**
 * Shared Resource Path Parser
 *
 * @author Adamancy Zhang at 12:56 on 2024-04-24
 */
@Component
public class SharedResourcePathParser {

    private static final int VALID_PATH_LENGTH = 3;

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
            return null;
        }
        String model = ps[1];
        String actionName = ps[2];
        Action firstAction = metaCacheManager.fetchAction(model, actionName);
        if (firstAction == null) {
            return null;
        }
        AccessResourceInfo info = new AccessResourceInfo(path);
        info.setModel(model);
        info.setActionName(actionName);
        String currentModel = model;
        for (int i = 3; i < psl; i++) {
            String p = ps[i];
            ResourcePath resourcePath = ResourcePathHelper.parsePath(info, currentModel, p);
            currentModel = ResourcePathHelper.convertModel(resourcePath);
            if (currentModel == null) {
                return null;
            }
            info.addPath(resourcePath);
        }
        return info;
    }
}
