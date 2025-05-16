package pro.shushi.pamirs.framework.faas.fun.builtin.business;

import pro.shushi.pamirs.framework.faas.spi.api.fun.BusinessFunctionsApi;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.CONTEXT;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.BUSINESS_CORP;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.BUSINESS_SHOP;

/**
 * 商业函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(NamespaceConstants.expression)
public class BusinessFunctions {

    @Function.Advanced(
            displayName = "获取当前用户的公司id", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_CORP_ID")
    @Function(name = "CURRENT_CORP_ID", scene = {BUSINESS_CORP}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_CORP_ID()\n函数说明: 获取当前用户的公司id"
    )
    public static Long currentCorpId() {
        return Spider.getDefaultExtension(BusinessFunctionsApi.class).currentCorpId();
    }

    @Function.Advanced(
            displayName = "获取当前用户的公司", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_CORP")
    @Function(name = "CURRENT_CORP", scene = {BUSINESS_CORP}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_CORP()\n函数说明: 获取当前用户的公司"
    )
    public static Object currentCorp() {
        return Spider.getDefaultExtension(BusinessFunctionsApi.class).currentCorp();
    }

    @Function.Advanced(
            displayName = "获取当前用户的店铺id", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_SHOP_ID")
    @Function(name = "CURRENT_SHOP_ID", scene = {BUSINESS_SHOP}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_SHOP_ID()\n函数说明: 获取当前用户的店铺id"
    )
    public static Long currentShopId() {
        return Spider.getDefaultExtension(BusinessFunctionsApi.class).currentShopId();
    }

    @Function.Advanced(
            displayName = "获取当前用户的店铺", language = JAVA,
            builtin = true, category = CONTEXT
    )
    @Function.fun("CURRENT_SHOP")
    @Function(name = "CURRENT_SHOP", scene = {BUSINESS_SHOP}, openLevel = LOCAL,
            summary = "函数示例: CURRENT_SHOP()\n函数说明: 获取当前用户的店铺"
    )
    public static Object currentShop() {
        return Spider.getDefaultExtension(BusinessFunctionsApi.class).currentShop();
    }

}
