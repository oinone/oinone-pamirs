package pro.shushi.pamirs.resource.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.constants.DefaultResourceConstants;
import pro.shushi.pamirs.resource.api.proxy.TranslationModuleDefinitionProxy;

import java.util.List;

@Component
@Model.model(TranslationModuleDefinitionProxy.MODEL_MODEL)
public class TranslationModuleDefinitionProxyAction {

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<TranslationModuleDefinitionProxy> queryPage(Pagination<TranslationModuleDefinitionProxy> page, IWrapper<TranslationModuleDefinitionProxy> queryWrapper) {

        String displayName = RSQLHelper.getFieldValue(TranslationModuleDefinitionProxy.MODEL_MODEL, queryWrapper.getOriginRsql(), TranslationModuleDefinitionProxy::getDisplayName).orElse("");
        LambdaQueryWrapper<TranslationModuleDefinitionProxy> qw = WrapperHelper.lambda(queryWrapper)
                .eq(TranslationModuleDefinitionProxy::getApplication, true);

        if (StringUtils.isEmpty(displayName) || DefaultResourceConstants.PUBLIC_RESOURCE_NAME.contains(displayName.replace("%", ""))) {
            qw.or(item -> item.eq(TranslationModuleDefinitionProxy::getModule, DefaultResourceConstants.PUBLIC_RESOURCE));
        }

        Pagination<TranslationModuleDefinitionProxy> translationModuleProxyPagination = new TranslationModuleDefinitionProxy().queryPage(page, qw);
        TranslationModuleDefinitionProxy translationModuleProxy = null;
        List<TranslationModuleDefinitionProxy> content = translationModuleProxyPagination.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            for (TranslationModuleDefinitionProxy moduleProxy : content) {
                if (DefaultResourceConstants.PUBLIC_RESOURCE.equals(moduleProxy.getModule())) {
                    translationModuleProxy = moduleProxy;
                }
            }
        }
        if (translationModuleProxy != null) {
            content.remove(translationModuleProxy);
            content.add(0, translationModuleProxy);
        }
        return translationModuleProxyPagination;
    }
}