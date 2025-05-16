package pro.shushi.pamirs.eip.api.constant;

import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * @author drome
 * @date 2021/8/28:09 下午
 */
public class EipSceneConstant {
    //实例生成的eip路由前缀
    public static final String ROUTE_NAME_PREFIX = "场景默认路由";
    public static final String ROUTE_INTERFACE_NAME_PREFIX = "default_scene_route";
    public static final String ROUTE_NAME_SEPARATOR = CharacterConstants.SEPARATOR_UNDERLINE;

    //实例生成的schedule
    public static final String SCHEDULE_INSTANCE_BODY = "SCHEDULE_INSTANCE_BODY";
//    public static final String SCHEDULE_INSTANCE_CONTEXT = "SCHEDULE_INSTANCE_CONTEXT";

    //实例调用上下文常量
    //增量日志
    public static final String INSTANCE_INC_UPDATE_LOG = "pamirs.eip.scene.instance.incUpdateLog";
    //再次执行. 应用于分页拉取数据
    public static final String INSTANCE_CYCLE = "pamirs.eip.scene.instance.cycle";

    public static final String CONVERT_FUNCTION_NAMESPACE_PREFIX = "pamirs.eip.default.scene.convert.namespace.";

    public interface ConvertFunctionName {
        String sourcePreConvert = "sourcePreConvert";
        String sourceCoreConvert = "sourceCoreConvert";
        String sourceAfterConvert = "sourceAfterConvert";
        String targetPreConvert = "targetPreConvert";
        String targetCoreConvert = "targetCoreConvert";
        String targetAfterConvert = "targetAfterConvert";
    }
//    public static final String DEFAULT_FUNCTION_NAME_SOURCE_PRE = "sourcePreConvert";
//    public static final String DEFAULT_FUNCTION_NAME_SOURCE_CORE = "sourceCoreConvert";
//    public static final String DEFAULT_FUNCTION_NAME_SOURCE_AFTER = "sourceAfterConvert";
//    public static final String DEFAULT_FUNCTION_NAME_TARGET_PRE = "targetPreConvert";
//    public static final String DEFAULT_FUNCTION_NAME_TARGET_CORE = "targetCoreConvert";
//    public static final String DEFAULT_FUNCTION_NAME_TARGET_AFTER = "targetAfterConvert";


}
