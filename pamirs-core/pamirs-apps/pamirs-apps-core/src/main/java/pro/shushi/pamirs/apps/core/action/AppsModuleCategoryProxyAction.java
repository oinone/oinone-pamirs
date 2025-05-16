package pro.shushi.pamirs.apps.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.apps.api.pmodel.AppsModuleCategoryProxy;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.modules.enmu.AppLikeEnum;
import pro.shushi.pamirs.boot.modules.enmu.AppStatusEnum;
import pro.shushi.pamirs.boot.modules.model.AppsModuleRelUser;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author drome
 * @date 2022/4/20下午8:02
 */
@Base
@Model.model(AppsModuleCategoryProxy.MODEL_MODEL)
public class AppsModuleCategoryProxyAction {

    @Function(openLevel = FunctionOpenEnum.API, summary = "获取应用分类树")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public List<AppsModuleCategoryProxy> fetchCateTree(AppsModuleCategoryProxy data) {
        List<AppsModuleCategoryProxy> result = new ArrayList<>();
        List<AppsModuleCategoryProxy> cates = new AppsModuleCategoryProxy().queryList();
        if (CollectionUtils.isEmpty(cates)) {
            return result;
        }

        Map<String, AppsModuleCategoryProxy> cateMap = cates.stream().collect(Collectors.toMap(AppsModuleCategoryProxy::getCode,i->i,(a,b)->a));

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

        String        displayName = data.getDisplayName();
        AppStatusEnum status      = data.getStatus();
        AppLikeEnum like        = data.getLike();

        //统计分类下的应用数量
        for (Map.Entry<String, AppsModuleCategoryProxy> entry : cateMap.entrySet()) {

            LambdaQueryWrapper<UeModule> qw = Pops.<UeModule>lambdaQuery().from(UeModule.MODEL_MODEL);
            if (StringUtils.isNotBlank(displayName)) {
                qw.like(UeModule::getDisplayName, displayName);
            }

            if (AppStatusEnum.INSTALLED.equals(status)) {
                qw.eq(UeModule::getState, ModuleStateEnum.INSTALLED.value());
            }

            if (AppStatusEnum.UNINSTALL.equals(status)) {
                qw.and(_q -> {
                    _q.ne(UeModule::getState, ModuleStateEnum.INSTALLED.value())
                            .or(_qw -> _qw.isNull(UeModule::getState));
                });
            }

            if (null != like) {
                if (CollectionUtils.isNotEmpty(likeModules)) {
                    switch (like) {
                        case LIKE:
                            qw.in(UeModule::getModule, likeModules);
                            break;
                        case NOT_LIKE:
                            qw.notIn(UeModule::getModule, likeModules);
                            break;
                    }
                } else {
                    // 没有收藏的应用
                    switch (like) {
                        case LIKE:
                            qw.in(UeModule::getModule, Collections.singletonList(""));
                            break;
                        case NOT_LIKE:
                            qw.notIn(UeModule::getModule, Collections.singletonList(""));
                            break;
                    }
                }
            }

            qw.eq(UeModule::getCategory, entry.getKey());
            long count = Models.origin().count(qw);
            entry.getValue().setModuleNum(count);
        }

        Map<String, List<AppsModuleCategoryProxy>> parentCode2Children = new HashMap<>();
        for (AppsModuleCategoryProxy cate : cates) {
            if (cate.getParent() == null || StringUtils.isEmpty(cate.getParent().getCode())) {
                result.add(cate);
                continue;
            }

            if (cate.getModuleNum() != 0) {
                parentCode2Children.computeIfAbsent(cate.getParent().getCode(), _k -> new ArrayList<>()).add(cate);
            }
        }

        // TODO: 2022/4/20 现在其实没层级,如果有,子分类的应用数,是否加入到父分类
        parentCode2Children.forEach((_k, _v) -> {
            AppsModuleCategoryProxy p = cateMap.get(_k);
            if (p != null) {
                p.setChildren(_v);
            }
        });

        return result;
    }
}
