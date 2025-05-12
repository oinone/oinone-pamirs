package pro.shushi.pamirs.meta.api.core.orm.systems;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 模型指令批量接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI
public interface ModelDirectiveBatchApi {

    /**
     * 清除配置
     */
    <T> T clear(T listOrObject);

    /**
     * 初始化配置
     */
    <T> T init(T listOrObject, SystemDirectiveEnum... directiveEnums);

    /**
     * 初始化配置
     */
    <T> T init(T listOrObject, Long directive);

    /**
     * 获取所有指令
     *
     * @return 指令
     */
    <T> Long value(T obj);

    /**
     * 禁用权限
     */
    <T> T sudo(T listOrObject);

    /**
     * 生效权限，默认生效
     */
    <T> T disableSudo(T listOrObject);

    /**
     * 是否禁用权限
     *
     * @return 禁用权限
     */
    <T> boolean isSudo(T listOrObject);

    /**
     * 生效乐观锁，默认生效
     */
    <T> T enableOptimisticLocker(T listOrObject);

    /**
     * 禁用乐观锁
     */
    <T> T disableOptimisticLocker(T listOrObject);

    /**
     * 是否生效乐观锁
     *
     * @return 乐观锁
     */
    <T> boolean isOptimisticLocker(T listOrObject);

    /**
     * 生效数据校验
     */
    <T> T enableCheck(T listOrObject);

    /**
     * 禁用数据校验，后端操作默认禁用
     */
    <T> T disableCheck(T listOrObject);

    /**
     * 是否进行数据校验
     *
     * @return 数据校验
     */
    <T> boolean isDoCheck(T listOrObject);

    /**
     * 生效默认值填充
     */
    <T> T enableDefaultValue(T listOrObject);

    /**
     * 禁用默认值填充，后端操作默认禁用
     */
    <T> T disableDefaultValue(T listOrObject);

    /**
     * 是否进行默认值填充
     *
     * @return 默认值填充
     */
    <T> boolean isDoDefaultValue(T listOrObject);

    /**
     * 生效扩展点
     */
    <T> T enableExtPoint(T listOrObject);

    /**
     * 禁用扩展点，默认执行扩展点
     */
    <T> T disableExtPoint(T listOrObject);

    /**
     * 是否执行扩展点
     *
     * @return 执行扩展点
     */
    <T> boolean isDoExtPoint(T listOrObject);

    /**
     * 生效拦截
     */
    <T> T enableHook(T listOrObject);

    /**
     * 禁用拦截，后端默认不拦截
     */
    <T> T disableHook(T listOrObject);

    /**
     * 是否拦截
     *
     * @return 执行扩展点
     */
    <T> boolean isHook(T listOrObject);

    /**
     * 生效持久化ORM字段别名
     */
    @SuppressWarnings("UnusedReturnValue")
    <T> T enableColumn(T listOrObject);

    /**
     * 禁用持久化ORM字段别名，后端操作默认禁用
     */
    @SuppressWarnings("UnusedReturnValue")
    <T> T disableColumn(T listOrObject);

    /**
     * 是否转换持久化ORM字段别名
     */
    <T> boolean isDoColumn(T listOrObject);

    /**
     * 标记前端请求
     */
    <T> T enableUsePkStrategy(T listOrObject);

    /**
     * 去掉前端请求标记
     */
    <T> T disableUsePkStrategy(T listOrObject);

    /**
     * 是否前端请求
     */
    <T> boolean isUsePkStrategy(T listOrObject);

    /**
     * 标记前端请求
     */
    <T> T enableFromClient(T listOrObject);

    /**
     * 去掉前端请求标记
     */
    <T> T disableFromClient(T listOrObject);

    /**
     * 是否前端请求
     */
    <T> boolean isFromClient(T listOrObject);

    /**
     * 标记内建动作
     */
    <T> T enableBuiltAction(T listOrObject);

    /**
     * 去掉内建动作标记
     */
    <T> T disableBuiltAction(T listOrObject);

    /**
     * 是否内建动作
     */
    <T> boolean isBuiltAction(T listOrObject);

    /**
     * 标记忽略函数管理器处理
     */
    <T> T enableIgnoreFunManagement(T listOrObject);

    /**
     * 去掉忽略函数管理器处理标记
     */
    <T> T disableIgnoreFunManagement(T listOrObject);

    /**
     * 是否忽略函数管理器处理
     */
    <T> boolean isIgnoreFunManagement(T listOrObject);

    /**
     * 标记重入
     */
    <T> T enableReentry(T listOrObject);

    /**
     * 去掉重入标记
     */
    <T> T disableReentry(T listOrObject);

    /**
     * 是否指令重入执行
     */
    <T> boolean isReentry(T listOrObject);

    /**
     * 标记ORM重入
     */
    <T> T enableOrmReentry(T listOrObject);

    /**
     * 去掉ORM重入标记
     */
    <T> T disableOrmReentry(T listOrObject);

    /**
     * 是否ORM指令重入执行
     */
    <T> boolean isOrmReentry(T listOrObject);

    /**
     * 标记数据为待持久化数据
     */
    <T> T enableDirty(T listOrObject);

    /**
     * 去掉待持久化标记
     */
    <T> T disableDirty(T listOrObject);

    /**
     * 是否待持久化数据
     */
    <T> boolean isDirty(T listOrObject);

    /**
     * 标记数据为已完成元数据计算
     */
    <T> T enableMetaCompleted(T listOrObject);

    /**
     * 去掉已完成元数据计算标记
     */
    <T> T disableMetaCompleted(T listOrObject);

    /**
     * 是否已完成元数据计算数据
     */
    <T> boolean isMetaCompleted(T listOrObject);

    /**
     * 标记数据为继承元数据
     */
    <T> T enableMetaInherited(T listOrObject);

    /**
     * 去掉元数据继承标记
     */
    <T> T disableMetaInherited(T listOrObject);

    /**
     * 是否继承元数据
     */
    <T> boolean isMetaInherited(T listOrObject);

    /**
     * 标记数据为元数据差量计算中
     */
    <T> T enableMetaDiffing(T listOrObject);

    /**
     * 去掉元数据差量计算中标记
     */
    <T> T disableMetaDiffing(T listOrObject);

    /**
     * 是否元数据差量计算中
     */
    <T> boolean isMetaDiffing(T listOrObject);

    /**
     * 标记数据为元数据为跨模型扩展
     */
    <T> T enableMetaCrossing(T listOrObject);

    /**
     * 去掉元数据跨模型扩展标记
     */
    <T> T disableMetaCrossing(T listOrObject);

    /**
     * 是否元数据为跨模型扩展
     */
    <T> boolean isMetaCrossing(T listOrObject);


    <T> T enableRemoteMeta(T listOrObject);
    <T> T disableRemoteMeta(T listOrObject);
    <T> boolean isRemoteMeta(T listOrObject);

}
