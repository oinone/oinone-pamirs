package pro.shushi.pamirs.boot.web.spi.api;

import pro.shushi.pamirs.boot.base.enmu.ActionTypeEnum;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

/**
 * 权限验证服务
 *
 * @author Adamancy Zhang at 11:08 on 2023-12-23
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface AuthVerificationApi {

    /**
     * 验证首页动作是否具有访问权限
     *
     * @param moduleDefinition 模块
     * @param model            动作模型
     * @param actionName       动作名称
     * @return 是否具有访问权限
     */
    boolean verifyHomepageActionAccess(ModuleDefinition moduleDefinition, String model, String actionName);

    /**
     * 验证模块及动作是否具有访问权限
     *
     * @param moduleDefinition 模块
     * @param actionType       动作类型
     * @param model            动作模型
     * @param actionName       动作名称
     * @return 是否具有访问权限
     */
    boolean verifyActionAccess(ModuleDefinition moduleDefinition, ActionTypeEnum actionType, String model, String actionName);
}
