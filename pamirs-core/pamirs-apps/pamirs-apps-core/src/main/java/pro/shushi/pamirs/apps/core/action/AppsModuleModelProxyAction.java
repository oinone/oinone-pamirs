package pro.shushi.pamirs.apps.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.pmodel.AppsModuleModelProxy;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Base
@Component
@Model.model(AppsModuleModelProxy.MODEL_MODEL)
public class AppsModuleModelProxyAction {

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<AppsModuleModelProxy> queryPage(Pagination<AppsModuleModelProxy> page, IWrapper<AppsModuleModelProxy> queryWrapper) {
        if(queryWrapper.getQueryData() == null){
            return Models.origin().queryPage(page, queryWrapper);
        }
        String rootModuleModule = (String) queryWrapper.getQueryData().get(
                LambdaUtil.fetchFieldName(AppsModuleModelProxy::getRootModuleModule)
        );
        if(StringUtils.isNotEmpty(rootModuleModule)){
            Set<String> dependencyModules = queryDependencyModules(rootModuleModule);
            ((QueryWrapper<AppsModuleModelProxy>)queryWrapper).in(
                    PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(AppsModuleModelProxy::getModule)), dependencyModules
            );
        }
        return Models.origin().queryPage(page, queryWrapper);
    }

    private Set<String> queryDependencyModules(String rootModuleModule) {
        Set<String> moduleDependencies = new HashSet<>();
        queryDependencyModules0(rootModuleModule, moduleDependencies);
        return moduleDependencies;
    }

    private void queryDependencyModules0(String module, Set<String> moduleDependencies){
        ModuleDefinition definition = PamirsSession.getContext().getModule(module);
        if(definition == null){
            return;
        }
        moduleDependencies.add(module);
        if(CollectionUtils.isEmpty(definition.getModuleDependencies())){
            return;
        }
        for (String moduleDependency : definition.getModuleDependencies()) {
            if(moduleDependencies.contains(moduleDependency)){
                continue;
            }
            queryDependencyModules0(moduleDependency,moduleDependencies);
        }
    }
}
