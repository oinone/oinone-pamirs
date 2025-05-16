package pro.shushi.pamirs.core.common;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.Optional;

/**
 * SceneUtil
 *
 * @author yakir on 2022/04/01 19:17.
 */
public class SceneUtils {

    private final static String SCENE = "scene";

    public static String getScene() {
        PamirsRequestVariables variables = PamirsSession.getRequestVariables();
        return Optional.ofNullable(variables)
                .map(PamirsRequestVariables::getParameterMap)
                .map(_pmap -> _pmap.get(SCENE))
                .filter(_arr -> _arr.length > 0)
                .map(_arr -> _arr[0])
                .orElse(null);
    }

    public static boolean isScene(String... scenes) {
        PamirsRequestVariables variables = PamirsSession.getRequestVariables();
        String _scene = Optional.ofNullable(variables)
                .map(PamirsRequestVariables::getParameterMap)
                .map(_pmap -> _pmap.get(SCENE))
                .filter(_arr -> _arr.length > 0)
                .map(_arr -> _arr[0])
                .orElse(null);
        return StringUtils.equalsAny(_scene, scenes);
    }
}
