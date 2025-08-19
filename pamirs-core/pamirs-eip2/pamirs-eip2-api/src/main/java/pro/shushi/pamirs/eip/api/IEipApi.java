package pro.shushi.pamirs.eip.api;

import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 所有接口基类
 *
 * @author Adamancy Zhang
 * @date 2020-07-06 10:01
 */
public interface IEipApi {

    /**
     * Camel上下文
     */
    EipCamelContext getContext();

    /**
     * 接口名称（唯一标识）
     *
     * @return 接口名称
     */
    @NotBlank
    String getInterfaceName();

    /**
     * 获取接口类型
     *
     * @return 接口类型
     */
    InterfaceTypeEnum getType();

    /**
     * 接口路由
     *
     * @return URI（Camel路由规则）
     */
    @NotBlank
    String getUri();

    /**
     * 交换模式枚举
     *
     * @return {@link ExchangePatternEnum#InOut} (default)
     * {@link ExchangePatternEnum#InOnly}
     * {@link ExchangePatternEnum#InOptionalOut}
     */
    @NotNull
    ExchangePatternEnum getExchangePattern();

    /**
     * 是否被数据库管理（内存中直接定义的对象均返回false，数据库中定义的对象均返回true）
     */
    @NotNull
    Boolean getIsDBManaged();

    /**
     * 是否开启日志功能
     *
     * @return true为启用, false为未启用
     */
    @Deprecated
    @NotNull
    Boolean getIsEnabledLog();

    /**
     * 是否被数据库管理（内存中直接定义的对象均返回false，数据库中定义的对象均返回true）
     */
    @NotNull
    Boolean getIsDBManaged();

    /**
     * 获取接口类别
     */
    String getCategory();
}
