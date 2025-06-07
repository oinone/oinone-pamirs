package pro.shushi.pamirs.framework.gateways.graph.java.utils;

import pro.shushi.pamirs.framework.gateways.graph.java.constants.MainSessionExtendConstants;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import static pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants.CAPITAL_MUTATION;
import static pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants.CAPITAL_QUERY;

/**
 * 请求上下文扩展帮助类
 * <p>
 * 2021/10/14 3:44 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SessionExtendUtils {

    public static void tagMainRequest() {
        PamirsSession.getKernelExtend().put(MainSessionExtendConstants.MAIN_REQUEST, Boolean.TRUE.toString());
    }

    public static boolean isMainRequest() {
        String mainRequest = PamirsSession.getKernelExtend().get(MainSessionExtendConstants.MAIN_REQUEST);
        return Boolean.TRUE.toString().equals(mainRequest);
    }

    public static String gqlSegmentNameToModelNameUtils(String gqlSegmentName) {
        if (gqlSegmentName.endsWith(CAPITAL_QUERY)) {
            return gqlSegmentName.substring(0, gqlSegmentName.lastIndexOf(CAPITAL_QUERY));
        } else if (gqlSegmentName.endsWith(CAPITAL_MUTATION)) {
            return gqlSegmentName.substring(0, gqlSegmentName.lastIndexOf(CAPITAL_MUTATION));
        }
        return gqlSegmentName;
    }

}
