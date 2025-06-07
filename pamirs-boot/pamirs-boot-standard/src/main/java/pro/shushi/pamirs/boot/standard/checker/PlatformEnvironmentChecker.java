package pro.shushi.pamirs.boot.standard.checker;

import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckResult;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.List;

/**
 * 平台环境检查API
 *
 * @author Adamancy Zhang at 12:54 on 2024-10-11
 */
public interface PlatformEnvironmentChecker {

    /**
     * 检查类型
     *
     * @return 类型标记
     */
    String type();

    /**
     * 收集环境信息
     *
     * @return 当前环境信息集合
     */
    List<PlatformEnvironment> collection();

    /**
     * 获取环境Key配置（当环境信息不在收集环境信息列表中时调用该方法获取环境Key）
     *
     * @param environment 环境信息
     * @return 环境Key
     */
    default EnvironmentKey getKey(PlatformEnvironment environment) {
        return null;
    }

    /**
     * 检查配置项是否过期
     *
     * @param environment 环境信息
     * @return 是否过期
     */
    EnvironmentCheckResult deprecated(PlatformEnvironment environment);

    /**
     * 检查环境信息，并返回最终需要保存的环境信息
     *
     * @param context             环境检查上下文
     * @param currentEnvironments 当前环境信息
     * @param historyEnvironments 历史环境信息
     * @return 检查通过后最终的环境信息
     */
    List<PlatformEnvironment> check(EnvironmentCheckContext context, List<PlatformEnvironment> currentEnvironments, List<PlatformEnvironment> historyEnvironments);

    /**
     * 检查环境信息前执行，每个检查器均会执行该方法
     *
     * @param context             环境检查上下文
     * @param currentEnvironments 当前检查类型对应的环境信息
     * @param historyEnvironments 当前检查类型对应的历史环境信息
     * @return 是否中断
     */
    default boolean checkBefore(EnvironmentCheckContext context,
                                List<PlatformEnvironment> currentEnvironments,
                                List<PlatformEnvironment> historyEnvironments) {
        return true;
    }

    /**
     * 检查环境信息后执行，每个检查器均会执行该方法，并返回最终需要保存的环境信息
     *
     * @param context             环境检查上下文
     * @param allEnvironments     全部环境信息
     * @param currentEnvironments 当前检查类型对应的环境信息
     * @param historyEnvironments 当前检查类型对应的历史环境信息
     * @return 检查通过后最终的环境信息
     */
    default List<PlatformEnvironment> checkAfter(EnvironmentCheckContext context,
                                                 List<PlatformEnvironment> allEnvironments,
                                                 List<PlatformEnvironment> currentEnvironments,
                                                 List<PlatformEnvironment> historyEnvironments) {
        return null;
    }

    /**
     * 保存环境信息
     *
     * @param context             当前上下文
     * @param allEnvironments     全部环境信息
     * @param currentEnvironments 当前检查类型对应的环境信息
     * @param historyEnvironments 当前检查类型对应的历史环境信息
     */
    default void save(EnvironmentCheckContext context, List<PlatformEnvironment> allEnvironments, List<PlatformEnvironment> currentEnvironments, List<PlatformEnvironment> historyEnvironments) {
        // do nothing.
    }

    /**
     * 添加创建项
     *
     * @param context            环境检查上下文
     * @param currentEnvironment 当前环境信息
     */
    default void addCreate(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addCreate(currentEnvironment);
    }

    /**
     * 添加更新项
     *
     * @param context            环境检查上下文
     * @param currentEnvironment 当前环境信息
     */
    default void addUpdate(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addUpdate(currentEnvironment);
    }

    /**
     * 添加删除项
     *
     * @param context            环境检查上下文
     * @param currentEnvironment 当前环境信息
     */
    default void addDelete(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addDelete(currentEnvironment);
    }

    /**
     * 添加错误项
     *
     * @param context            环境检查上下文
     * @param currentEnvironment 当前环境信息
     */
    default void addError(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addError(currentEnvironment);
    }

    default void addError(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        context.addError(currentEnvironment, message);
    }

    /**
     * 添加警告项
     *
     * @param context            环境检查上下文
     * @param currentEnvironment 当前环境信息
     */
    default void addWarning(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addWarning(currentEnvironment);
    }

    default void addWarning(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        context.addWarning(currentEnvironment, message);
    }

    /**
     * 添加过期项
     *
     * @param context            环境检查上下文
     * @param currentEnvironment 当前环境信息
     */
    default void addDeprecated(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment) {
        context.addDeprecated(currentEnvironment);
    }

    default void addDeprecated(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, String message) {
        context.addDeprecated(currentEnvironment, message);
    }

}
