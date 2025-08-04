package pro.shushi.pamirs.sso.oauth2.server.action;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.sso.api.constant.SsoConfigurationConstant;
import pro.shushi.pamirs.sso.api.model.SsoOauth2ClientDetails;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoOauth2ClientDetailsService;
import pro.shushi.pamirs.user.api.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@Model.model(SsoOauth2ClientDetails.MODEL_MODEL)
public class SsoOauth2ClientDetailsAction {

    @Autowired
    private SsoOauth2ClientDetailsService ssoOauth2ClientDetailsService;

    @Action(displayName = "随机生成", contextType = ActionContextTypeEnum.CONTEXT_FREE)
    public SsoOauth2ClientDetails saveSsoOauth2ClientDetails() {
        SsoOauth2ClientDetails ssoOauth2ClientDetails = ssoOauth2ClientDetailsService.AutoGenerateClientDetails();
        return ssoOauth2ClientDetails;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    public SsoOauth2ClientDetails deleteMultipleOrSingleIds(List<SsoOauth2ClientDetails> ssoOauth2ClientDetailsList) {
        ssoOauth2ClientDetailsService.deleteMultipleOrSingleIds(ssoOauth2ClientDetailsList);
        return new SsoOauth2ClientDetails();
    }

    @Function.Advanced(displayName = "查询所有SSO应用", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<SsoOauth2ClientDetails> queryPage(Pagination<SsoOauth2ClientDetails> page, IWrapper<SsoOauth2ClientDetails> queryWrapper) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = CookieUtil.getValue(request, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
        Pagination<SsoOauth2ClientDetails> pagination = new SsoOauth2ClientDetails().queryPage(page, queryWrapper);
        pagination.getContent().forEach(item -> item.setHomepageUrl(item.getHomepageUrl() + "?accessToken=" + token));
        return pagination;
    }


    @Action.Advanced(type = FunctionTypeEnum.UPDATE, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public SsoOauth2ClientDetails update(SsoOauth2ClientDetails data) {
        if (data != null && data.getId() != null) {
            data.updateById();
        }
        return data;
    }


}
