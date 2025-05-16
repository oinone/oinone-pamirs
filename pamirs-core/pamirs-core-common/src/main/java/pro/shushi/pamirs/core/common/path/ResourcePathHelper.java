package pro.shushi.pamirs.core.common.path;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.loader.path.AccessResourceInfo;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePath;
import pro.shushi.pamirs.boot.web.loader.path.ResourcePathMetadataType;
import pro.shushi.pamirs.boot.web.manager.MetaCacheManager;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * Resource Path Helper
 *
 * @author Adamancy Zhang at 13:01 on 2024-04-24
 */
public class ResourcePathHelper {

    private ResourcePathHelper() {
        //reject create object
    }

    public static ResourcePath parsePath(AccessResourceInfo info, String model, String path) {
        int index = path.indexOf(ResourcePath.TYPE_SPLIT);
        if (index == -1) {
            return new ResourcePath(ResourcePathMetadataType.VIEW, model, path);
        }
        ResourcePathMetadataType type = ResourcePathMetadataType.of(path.substring(0, index));
        if (type == null) {
            throw PamirsException.construct(BootUxdExpEnumerate.RESOURCE_PATH_PARSER_ERROR).errThrow();
        }
        boolean isSameModel = false;
        int nextIndex = index + 1;
        int modelSplitIndex = path.indexOf(ResourcePath.TYPE_SPLIT, nextIndex);
        if (modelSplitIndex == -1) {
            throw PamirsException.construct(BootUxdExpEnumerate.RESOURCE_PATH_PARSER_ERROR).errThrow();
        }
        ResourcePath lastPath = info.getLastPath();
        if (lastPath != null) {
            int sameSplitIndex = path.indexOf(ResourcePath.SAME_MODEL_FLAG, nextIndex);
            if (sameSplitIndex == -1) {
                model = path.substring(nextIndex, modelSplitIndex);
            } else {
                model = lastPath.getModel();
                isSameModel = true;
            }
        } else {
            model = path.substring(nextIndex, modelSplitIndex);
        }
        String name = path.substring(modelSplitIndex + 1);
        return new ResourcePath(type, model, name, isSameModel);
    }

    public static String convertModel(ResourcePath resourcePath) {
        ResourcePathMetadataType type = resourcePath.getType();
        switch (type) {
            case VIEW:
                return resourcePath.getModel();
            case ACTION:
                return getActionModel(CommonApiFactory.getApi(MetaCacheManager.class).fetchAction(resourcePath.getModel(), resourcePath.getName()));
            case FIELD:
                return getFieldModel(PamirsSession.getContext().getModelField(resourcePath.getModel(), resourcePath.getName()));
            default:
                throw new IllegalArgumentException("Invalid resource path metadata type. value = " + type);
        }
    }

    private static String getActionModel(Action action) {
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

    private static String getFieldModel(ModelFieldConfig modelField) {
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
