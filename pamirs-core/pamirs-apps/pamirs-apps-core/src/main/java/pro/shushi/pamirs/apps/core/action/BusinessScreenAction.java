package pro.shushi.pamirs.apps.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.apps.api.pmodel.AppsManagementModule;
import pro.shushi.pamirs.apps.api.pmodel.AppsModuleCategoryProxy;
import pro.shushi.pamirs.apps.api.tmodel.BusinessScreenTransient;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据大屏
 *
 * @author shier
 * date  2021/5/26 2:40 下午
 */
@Base
@Model.model(BusinessScreenTransient.MODEL_MODEL)
public class BusinessScreenAction {

    @Function(openLevel = FunctionOpenEnum.API, summary = "业务大屏构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY, displayName = "业务大屏")
    public BusinessScreenTransient construct(BusinessScreenTransient businessScreen) {
        BusinessScreenTransient result = new BusinessScreenTransient();

        //查询全部有分组的应用
        List<AppsManagementModule> appsManagementModules = Models.origin().queryListByWrapper(
                Pops.<AppsManagementModule>lambdaQuery()
                        .from(AppsManagementModule.MODEL_MODEL)
                        .isNotNull(AppsManagementModule::getCategory)
        );
        if (CollectionUtils.isEmpty(appsManagementModules)) {
            return result;
        }
        Map<String, List<AppsManagementModule>> codeMapModuleList = appsManagementModules
                .stream()
                .filter(i -> StringUtils.isNotBlank(i.getCategory()))
                .collect(Collectors.groupingBy(AppsManagementModule::getCategory));

        //查询全部分类
        List<AppsModuleCategoryProxy> appsModuleCategories = Models.origin().queryListByWrapper(
                Pops.<AppsModuleCategoryProxy>lambdaQuery()
                        .from(AppsModuleCategoryProxy.MODEL_MODEL)
                        .ne(AppsModuleCategoryProxy::getScreenVisible, Boolean.FALSE)//过滤明确标记为业务大屏不展示的类目
        );
        if (CollectionUtils.isEmpty(appsModuleCategories)) {
            return result;
        }
        Map<String, AppsModuleCategoryProxy> codeMapCategoryAll = appsModuleCategories
                .stream()
                .collect(Collectors.toMap(AppsModuleCategoryProxy::getCode, i -> i));

        Map<String, AppsModuleCategoryProxy> codeMapCategory = findUsefulCategories(codeMapCategoryAll, codeMapModuleList);

        //构造分类列表
        List<AppsModuleCategoryProxy> categories = new ArrayList<>();
        for (AppsModuleCategoryProxy v : codeMapCategory.values()) {
            if (v.getParent() == null || StringUtils.isBlank(v.getParent().getCode())) {
                categories.add(v);
                continue;
            }
            AppsModuleCategoryProxy parent = codeMapCategory.get(v.getParent().getCode());
            if (parent == null) {
                categories.add(v);
                continue;
            }
            List<AppsModuleCategoryProxy> appChildren = parent.getChildren();
            if (appChildren == null) {
                appChildren = new ArrayList<>();
                parent.setChildren(appChildren);
            }
            appChildren.add(v);
        }

        //排序
        sort(categories);
        result.setCategories(categories);
        return result;
    }

    private Map<String, AppsModuleCategoryProxy> findUsefulCategories(Map<String, AppsModuleCategoryProxy> codeMapCategoryAll, Map<String, List<AppsManagementModule>> codeMapModuleList) {
        Map<String, AppsModuleCategoryProxy> result = new HashMap<>();
        for (String moduleCateCode : codeMapModuleList.keySet()) {
            findUsefulCategories0(result, codeMapCategoryAll, codeMapModuleList, moduleCateCode);
        }
        return result;
    }

    private void findUsefulCategories0(Map<String, AppsModuleCategoryProxy> result,
                                       Map<String, AppsModuleCategoryProxy> codeMapCategoryAll,
                                       Map<String, List<AppsManagementModule>> codeMapModuleList,
                                       String cateCode) {
        AppsModuleCategoryProxy category = codeMapCategoryAll.get(cateCode);
        if (category == null) {
            return;
        }
        category.setAppModules(codeMapModuleList.get(cateCode));
        result.put(category.getCode(), category);

        if (category.getParent() != null && StringUtils.isNotBlank(category.getParent().getCode())) {
            findUsefulCategories0(result, codeMapCategoryAll, codeMapModuleList, category.getParent().getCode());
        }
    }


    private void sort(List<AppsModuleCategoryProxy> categories) {
        categories.sort(Comparator.comparing(AppsModuleCategoryProxy::getSequence));
        for (AppsModuleCategoryProxy c : categories) {
            if (CollectionUtils.isNotEmpty(c.getAppModules())) {
                c.getAppModules().sort(Comparator.comparing(AppsManagementModule::getPriority));
            }
            if (CollectionUtils.isNotEmpty(c.getChildren())) {
                sort(c.getChildren());
            }
        }
    }
}
