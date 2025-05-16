package pro.shushi.pamirs.sys.setting.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.proxy.AdvancedHomeUeModuleProxy;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

@Component
@Model.model(AdvancedHomeUeModuleProxy.MODEL_MODEL)
public class AdvancedHomeUeModuleProxyAction {

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AdvancedHomeUeModuleProxy> queryPage(Pagination<AdvancedHomeUeModuleProxy> page, IWrapper<AdvancedHomeUeModuleProxy> queryWrapper) {
        LambdaQueryWrapper<AdvancedHomeUeModuleProxy> qw = WrapperHelper.lambda(queryWrapper).eq(AdvancedHomeUeModuleProxy::getApplication, Boolean.TRUE);
        Pagination<AdvancedHomeUeModuleProxy> translationModuleProxyPagination = new AdvancedHomeUeModuleProxy().queryPage(page, qw);
        List<AdvancedHomeUeModuleProxy> content = translationModuleProxyPagination.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            for (AdvancedHomeUeModuleProxy moduleProxy : content) {
                if (StringUtils.isBlank(moduleProxy.getLogo())) {
                    String logoFormat = FileClientFactory.getClient().getStaticUrl() + "/oinone/static/images/default.png";
                    moduleProxy.setLogo(logoFormat);
                }
            }
        }
        return translationModuleProxyPagination;
    }
}

