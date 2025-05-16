package pro.shushi.pamirs.apps.core.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.apps.api.pmodel.BusinessScreenRelation;
import pro.shushi.pamirs.apps.api.tmodel.ModuleRelationPath;
import pro.shushi.pamirs.apps.core.util.ModuleRelationAdapter;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDependency;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2020/4/20
 */
@Base
@Component
@Model.model(BusinessScreenRelation.MODEL_MODEL)
public class BusinessScreenRelationAction {

    @Autowired
    private ModuleRelationAdapter moduleRelationAdapter;

    @Function(openLevel = FunctionOpenEnum.API, summary = " 查询模块依赖关系")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public BusinessScreenRelation fetchRelation(BusinessScreenRelation data) throws Exception {
        if (null == data || null == data.getModule()) {
            return data;
        }
        List<BusinessScreenRelation> allModules = Models.origin().queryListByWrapper(
                Pops.<BusinessScreenRelation>query()
                        .from(BusinessScreenRelation.MODEL_MODEL)
                        .select(
                                fetchSelect(BusinessScreenRelation::getId),
                                fetchSelect(BusinessScreenRelation::getModule),
                                fetchSelect(BusinessScreenRelation::getDisplayName),
                                fetchSelect(BusinessScreenRelation::getLogo),
                                fetchSelect(BusinessScreenRelation::getState),
                                fetchSelect(BusinessScreenRelation::getApplication),
                                fetchSelect(BusinessScreenRelation::getModuleDependencies),
                                fetchSelect(BusinessScreenRelation::getModuleExclusions)
                        )
        );
        // TODO: 2022/4/26 查中间表
        allModules = moduleRelationAdapter.convertModulesRelation(allModules);

        Map<String, BusinessScreenRelation> moduleMap = allModules.stream().collect(Collectors.toMap(BusinessScreenRelation::getModule, i -> i));
        BusinessScreenRelation result = moduleMap.get(data.getModule());
        if (result == null) {
            return result;
        }

        for (BusinessScreenRelation module : allModules) {
            if (module.getModuleDependencyList() != null) {
                for (ModuleDependency moduleDependency : module.getModuleDependencyList()) {
                    BusinessScreenRelation down = moduleMap.get(moduleDependency.getDependencyModule());
                    if (down == null) {
                        continue;
                    }
                    //将对方加入自己的down列表,将自己加入对方up的列表
                    module.addDownRelation(down);
                    down.addUpRelation(module);
                }
                module.setDownRelationList(
                        module.getModuleDependencyList().stream().map(_d -> moduleMap.get(_d.getDependencyModule())).filter(Objects::nonNull).collect(Collectors.toList())
                );
            }
        }

        buildGraph(result, BusinessScreenRelation::getDownRelationList);
        buildGraph(result, BusinessScreenRelation::getUpRelationList);
        return result;
    }


    private void buildGraph(BusinessScreenRelation module, Getter<BusinessScreenRelation, List<BusinessScreenRelation>> relationFieldGetter) {
        Map<String, UeModule> nodes = new HashMap<>();
        List<ModuleRelationPath> paths = new ArrayList<>();
        buildGraph0(null, module, nodes, paths, relationFieldGetter);
        if (LambdaUtil.fetchFieldName(relationFieldGetter).equals(LambdaUtil.fetchFieldName(BusinessScreenRelation::getDownRelationList))) {
            module.setDownNodes(new ArrayList<>(nodes.values()));
            module.setDownPaths(paths);
        } else {
            module.setUpNodes(new ArrayList<>(nodes.values()));
            module.setUpPaths(paths);
        }
    }

    private void buildGraph0(BusinessScreenRelation parent, BusinessScreenRelation current, Map<String, UeModule> nodes, List<ModuleRelationPath> paths, Getter<BusinessScreenRelation, List<BusinessScreenRelation>> relationFieldGetter) {
        if (parent != null) {
            ModuleRelationPath path = new ModuleRelationPath();
            path.setFromModule(parent.getModule());
            path.setToModule(current.getModule());
            paths.add(path);

            if (nodes.containsKey(current.getModule())) {
                //节点处理过
                return;
            }
            BusinessScreenRelation localM = new BusinessScreenRelation();
            //必要属性赋值
            localM.setId(current.getId());
            localM.setModule(current.getModule());
            localM.setDisplayName(current.getDisplayName());
            localM.setLogo(current.getLogo());
            localM.setState(current.getState());
            localM.setApplication(current.getApplication());
            nodes.put(current.getModule(), localM);
        }

        List<BusinessScreenRelation> relations = relationFieldGetter.apply(current);
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        for (BusinessScreenRelation child : relations) {
            buildGraph0(current, child, nodes, paths, relationFieldGetter);
        }
    }

    private String fetchSelect(Getter<BusinessScreenRelation, Object> getter) {
        String fieldName = LambdaUtil.fetchFieldName(getter);
        return
                PStringUtils.fieldName2Column(fieldName) + " as " + fieldName;
    }
}
