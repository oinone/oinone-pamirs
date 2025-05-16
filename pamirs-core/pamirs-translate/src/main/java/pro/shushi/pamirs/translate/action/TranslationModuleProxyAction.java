package pro.shushi.pamirs.translate.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.translate.constant.TranslateConstants;
import pro.shushi.pamirs.translate.proxy.TranslationModuleProxy;

import java.util.List;

@Component
@Model.model(TranslationModuleProxy.MODEL_MODEL)
public class TranslationModuleProxyAction {

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<TranslationModuleProxy> queryPage(Pagination<TranslationModuleProxy> page, IWrapper<TranslationModuleProxy> queryWrapper) {
        String displayName = RSQLHelper.getFieldValue(TranslationModuleProxy.MODEL_MODEL, queryWrapper.getOriginRsql(), TranslationModuleProxy::getDisplayName).orElse("");
        LambdaQueryWrapper<TranslationModuleProxy> qw = WrapperHelper.lambda(queryWrapper)
                .eq(TranslationModuleProxy::getApplication, true);

        if (StringUtils.isEmpty(displayName) || TranslateConstants.PUBLIC_RESOURCE_NAME.contains(displayName.replace("%", ""))) {
            qw.or(item -> item.eq(TranslationModuleProxy::getModule, TranslateConstants.PUBLIC_RESOURCE));
        }

        Pagination<TranslationModuleProxy> translationModuleProxyPagination = new TranslationModuleProxy().queryPage(page, qw);
        TranslationModuleProxy translationModuleProxy = null;
        List<TranslationModuleProxy> content = translationModuleProxyPagination.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            for (TranslationModuleProxy moduleProxy : content) {
                if (TranslateConstants.PUBLIC_RESOURCE.equals(moduleProxy.getModule())) {
                    translationModuleProxy = moduleProxy;
                }
                String logo = moduleProxy.getLogo();
                if (StringUtils.isBlank(logo)) {
                    logo = FileClientFactory.getClient().getStaticUrl() + "/oinone/static/images/default.png";
                }
                moduleProxy.setLogo(logo);
            }
        }
        if (translationModuleProxy != null) {
            content.remove(translationModuleProxy);
            content.add(0, translationModuleProxy);
        }
        return translationModuleProxyPagination;
    }
}