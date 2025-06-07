package pro.shushi.pamirs.auth.api.utils;

import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.session.AccessResourceInfoSession;

import java.util.Optional;

/**
 * 会话路径帮助类
 *
 * @author Adamancy Zhang at 21:50 on 2024-04-23
 */
public class SessionPathHelper {

    private SessionPathHelper() {
        // reject create object
    }

    public static String getActionPath(String model, String name) {
        return Optional.ofNullable(AccessResourceInfoSession.getInfo()).filter(v -> {
            ResourcePath lastPath = v.getLastPath();
            if (lastPath == null) {
                ViewAction viewAction = v.getViewAction();
                if (viewAction != null) {
                    return model.equals(viewAction.getModel()) && name.equals(viewAction.getName());
                }
                return model.equals(v.getModel()) && name.equals(v.getActionName());
            }
            return ResourcePathMetadataType.ACTION.equals(lastPath.getType()) && model.equals(lastPath.getModel()) && name.equals(lastPath.getName());
        }).map(AccessResourceInfo::toString).orElse(null);
    }
}
