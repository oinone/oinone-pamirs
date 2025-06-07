package pro.shushi.pamirs.resource.core.action;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.model.ResourceLang;
import pro.shushi.pamirs.resource.api.proxy.TranslationResourceLangProxy;

/**
 * Copyright 2024 The Netty Project
 *
 * @author rjn
 */
@Component
@Model.model(TranslationResourceLangProxy.MODEL_MODEL)
public class TranslationResourceLangProxyAction {
    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<TranslationResourceLangProxy> queryPage(Pagination<TranslationResourceLangProxy> page, IWrapper<TranslationResourceLangProxy> queryWrapper) {
        LambdaQueryWrapper<TranslationResourceLangProxy> qw = WrapperHelper.lambda(queryWrapper).ne(ResourceLang::getCode, "zh-CN");
        return new TranslationResourceLangProxy().queryPage(page, qw);
    }
}
