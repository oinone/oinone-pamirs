package pro.shushi.pamirs.apps.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.enmu.ModuleTypeEnum;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.apps.api.pmodel.AppsModuleModelProxy;
import pro.shushi.pamirs.apps.api.service.AppsManagementModuleService;
import pro.shushi.pamirs.apps.core.util.ModuleRelationAdapter;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.boot.modules.enmu.AppLikeEnum;
import pro.shushi.pamirs.boot.modules.enmu.AppStatusEnum;
import pro.shushi.pamirs.boot.modules.model.AppsModuleRelUser;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.core.common.api.EditionService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用管理能力
 *
 * @author shier
 * date  2021/5/26 10:57 上午
 */
@Slf4j
@Base
@Component
@Model.model(AppsManagementModule.MODEL_MODEL)
public class AppsManagementModuleAction {

    @Autowired
    private ModuleRelationAdapter moduleRelationAdapter;

    @Autowired
    private AppsManagementModuleService appsManagementModuleService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private EditionService editionService;

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public AppsManagementModule construct(AppsManagementModule appsManagementModule) {
        appsManagementModule.setEnterpriseEdition(editionService.checkEdition());
        return appsManagementModule;
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public Pagination<AppsManagementModule> queryPage(Pagination<AppsManagementModule> page, IWrapper<AppsManagementModule> queryWrapper) {
        LambdaQueryWrapper<AppsManagementModule> qw = ((QueryWrapper<AppsManagementModule>) queryWrapper).lambda();
        Map<String, Object> queryData = queryWrapper.getQueryData();
        AppLikeEnum like = null;
        AppStatusEnum status = null;
        if (queryData != null && queryData.size() > 0) {
            String appLikeEnumName = (String) queryData.get(LambdaUtil.fetchFieldName(AppsManagementModule::getLike));
            like = Optional.ofNullable(appLikeEnumName)
                    .filter(StringUtils::isNotBlank)
                    .map(AppLikeEnum::valueOf)
                    .orElse(null);
            String appStatusEnumName = (String) queryData.get(LambdaUtil.fetchFieldName(AppsManagementModule::getStatus));
            status = Optional.ofNullable(appStatusEnumName)
                    .filter(StringUtils::isNotBlank)
                    .map(AppStatusEnum::valueOf)
                    .orElse(null);
        }

        // 获取用户收藏的应用列表
        Set<String> likeModules = Models.origin()
                .queryListByWrapper(new Pagination<AppsModuleRelUser>().setSize(Long.MAX_VALUE),
                        Pops.<AppsModuleRelUser>lambdaQuery()
                                .from(AppsModuleRelUser.MODEL_MODEL)
                                .eq(AppsModuleRelUser::getUserId, PamirsSession.getUserId())
                                .eq(AppsModuleRelUser::getLike, true)
                )
                .stream()
                .map(AppsModuleRelUser::getModule)
                .collect(Collectors.toSet());
        if (null != like) {
            if (CollectionUtils.isNotEmpty(likeModules)) {
                switch (like) {
                    case LIKE:
                        qw.in(AppsManagementModule::getModule, likeModules);
                        break;
                    case NOT_LIKE:
                        qw.notIn(AppsManagementModule::getModule, likeModules);
                        break;
                }
            } else {
                // 没有收藏的应用
                switch (like) {
                    case LIKE:
                        qw.in(AppsManagementModule::getModule, Collections.singletonList(""));
                        break;
                    case NOT_LIKE:
                        qw.notIn(AppsManagementModule::getModule, Collections.singletonList(""));
                        break;
                }
            }
        }

        if (AppStatusEnum.INSTALLED.equals(status)) {
            qw.eq(AppsManagementModule::getState, ModuleStateEnum.INSTALLED.value());
        }
        if (AppStatusEnum.UNINSTALL.equals(status)) {
            qw.and(_q -> {
                _q.ne(AppsManagementModule::getState, ModuleStateEnum.INSTALLED.value())
                        .or(_qw -> _qw.isNull(AppsManagementModule::getState));
            });
        }

        Pagination<AppsManagementModule> resultPage = Models.origin().queryPage(page, qw);
        List<AppsManagementModule> dataList = resultPage.getContent();
        if (CollectionUtils.isEmpty(dataList)) {
            return resultPage;
        }
        for (AppsManagementModule module : dataList) {
            if (module.getLatestVersion() != null && !module.getLatestVersion().equals(module.getPlatformVersion()) && ModuleStateEnum.INSTALLED.equals(module.getState())) {
                module.setCanUpgrade(Boolean.TRUE);
            }
            if (likeModules.contains(module.getModule())) {
                module.setLike(AppLikeEnum.LIKE);
            }
        }
        return resultPage;
    }


    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API})
    public AppsManagementModule queryOne(AppsManagementModule query) {
        AppsManagementModule result = Models.origin().queryOne(query);
        if (result == null) {
            return null;
        }
        // 编辑/详情 回填代理数据
        result = moduleRelationAdapter.convertAppsModuleRelation(result);

        if (Boolean.TRUE.equals(result.getApplication())) {
            result.setModuleType(ModuleTypeEnum.APPLICATION);
        } else {
            result.setModuleType(ModuleTypeEnum.MODULE);
        }
        // 绑定首页回填代理数据
        if (StringUtils.isNotEmpty(result.getHomePageModel())) {
            result.setBindHomePageModel(
                    new AppsModuleModelProxy().setModel(result.getHomePageModel()).queryOne()
            );
        }
        result.setHomePageModel(result.getHomePageModel());
        result.setHomePageName(result.getHomePageName());
        ViewAction homePage = result.fieldQuery(AppsManagementModule::getHomePage).getHomePage();
        if (homePage != null) {
            result.setBindHomePageView(
                    homePage.fieldQuery(ViewAction::getResView).getResView()
            );
        }
        result.fieldQuery(AppsManagementModule::getUrlHomePage);
        return result;
    }

    @Action.Advanced(managed = true, invisible = ExpConstants.idValueExist, type = FunctionTypeEnum.CREATE)
    @Action(displayName = "创建", label = "确定", summary = "添加", bindingType = ViewTypeEnum.FORM)
    public AppsManagementModule create(AppsManagementModule data) {
        return appsManagementModuleService.create(data);
    }

    @Action.Advanced(managed = true, invisible = ExpConstants.idValueNotExist)
    @Action(displayName = "更新", label = "确定", summary = "修改", bindingType = ViewTypeEnum.FORM)
    public AppsManagementModule update(AppsManagementModule data) {
        return appsManagementModuleService.update(data);
    }

    @Action(displayName = "绑定首页")
    public AppsManagementModule bindHomePage(AppsManagementModule data) {
        return appsManagementModuleService.bindHomePage(data);
    }

    @Action(displayName = "安装")
    @Action.Advanced(invisible = "activeRecord.state != 'UNINSTALLED'")
    public AppsManagementModule install(AppsManagementModule module) {
        return appsManagementModuleService.install(module);
    }

    @Action(displayName = "升级")
    @Action.Advanced(invisible = "activeRecord.state != 'INSTALLED'")
    public AppsManagementModule upgrade(AppsManagementModule module) {
        return appsManagementModuleService.upgrade(module);
    }

    @Action(displayName = "重启")
    @Action.Advanced(invisible = "activeRecord.state != 'INSTALLED'")
    public AppsManagementModule reload(AppsManagementModule module) {
        return appsManagementModuleService.reload(module);
    }

    @Action(displayName = "卸载")
    @Action.Advanced(invisible = "activeRecord.state != 'INSTALLED'")
    public AppsManagementModule uninstall(AppsManagementModule module) {
        return appsManagementModuleService.uninstall(module);
    }
}
