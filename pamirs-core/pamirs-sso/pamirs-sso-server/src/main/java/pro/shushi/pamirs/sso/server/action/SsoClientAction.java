package pro.shushi.pamirs.sso.server.action;

import jakarta.servlet.http.HttpServletRequest;
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
import pro.shushi.pamirs.sso.api.model.SsoClient;
import pro.shushi.pamirs.sso.server.model.SsoClientService;
import pro.shushi.pamirs.user.api.utils.CookieUtil;

import java.util.List;

@Component
@Model.model(SsoClient.MODEL_MODEL)
public class SsoClientAction {

    @Autowired
    private SsoClientService ssoClientService;

    @Action.Advanced(name = FunctionConstants.create, managed = true, invisible = ExpConstants.idValueExist)
    @Action(displayName = "保存", summary = "创建", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.create)
    @Function.fun(FunctionConstants.create)
    public SsoClient create(SsoClient data) {
        ssoClientService.AutoGenerateClient(data);
        return data;
    }

    @Action(displayName = "删除", contextType = ActionContextTypeEnum.SINGLE_AND_BATCH)
    @Function(name = FunctionConstants.delete)
    @Function.fun(FunctionConstants.deleteWithFieldBatch)
    @Function.Advanced(type = FunctionTypeEnum.DELETE)
    public List<SsoClient> delete(List<SsoClient> dataList) {
        ssoClientService.deleteMultipleOrSingleIds(dataList);
        return dataList;
    }

    @Function.Advanced(displayName = "查询列表", type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<SsoClient> queryPage(Pagination<SsoClient> page, IWrapper<SsoClient> queryWrapper) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = CookieUtil.getValue(request, SsoConfigurationConstant.PAMIRS_SSO_LOGIN_KEY);
        Pagination<SsoClient> pagination = new SsoClient().queryPage(page, queryWrapper);
        pagination.getContent().forEach(item -> item.setHomepageUrl(item.getHomepageUrl() + "?accessToken=" + token));
        return pagination;
    }

    @Function.Advanced(type = FunctionTypeEnum.UPDATE)
    @Action.Advanced(name = FunctionConstants.update, managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", summary = "修改", bindingType = ViewTypeEnum.FORM)
    @Function(name = FunctionConstants.update)
    @Function.fun(FunctionConstants.update)
    public SsoClient update(SsoClient data) {
        if (data != null && data.getId() != null) {
            data.updateById();
        }
        return data;
    }

}
