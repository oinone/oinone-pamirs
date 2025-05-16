package pro.shushi.pamirs.eip.view.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrationInterfaceService;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPrepareChain;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPreparer;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 集成接口定义
 */
@Component
public class EipIntegrationInterfaceEditManager {

    @Autowired
    private EipIntegrationInterfaceService eipIntegrationInterfaceService;

    public boolean createByInterfaceEdit(EipIntegrationInterfaceEdit interfaceEdit) {
        EipInterfaceEditPrepareChain<EipIntegrationInterface> prepareChain = fetchPrepareChain(eipIntegrationInterface -> eipIntegrationInterfaceService.create(eipIntegrationInterface));
        prepareChain.prepare(interfaceEdit,prepareChain);
        return true;
    }

    public boolean updateByInterfaceEdit(EipIntegrationInterfaceEdit interfaceEdit) {
        EipInterfaceEditPrepareChain<Integer> prepareChain = fetchPrepareChain(eipIntegrationInterface -> eipIntegrationInterfaceService.updateById(eipIntegrationInterface));
        prepareChain.prepare(interfaceEdit,prepareChain);
        return true;
    }

    public EipIntegrationInterfaceEdit construct(EipIntegrationInterface integrationInterface) {
        EipInterfaceEditPrepareChain<EipIntegrationInterfaceEdit> prepareChain = fetchConstructChain();
        return prepareChain.construct(integrationInterface, prepareChain);
    }

    public <R> EipInterfaceEditPrepareChain<R> fetchPrepareChain(Function<EipIntegrationInterface,R> function) {
        EipInterfaceEditPrepareChain<R> prepareChain = EipInterfaceEditPrepareChain
                .<R>build(function);
        fullPreparer(prepareChain);
        return prepareChain;
    }

    public <R> EipInterfaceEditPrepareChain<R> fetchConstructChain() {
        EipInterfaceEditPrepareChain<R> constructChain = EipInterfaceEditPrepareChain
                .<R>build();
        fullPreparer(constructChain);
        return constructChain;
    }


    public static FunctionDefinition fetchFun(String nameSpace, String fun) {
        if (!StringUtils.isNoneBlank(nameSpace,fun)) return null;
        LambdaQueryWrapper<FunctionDefinition> wrapper = Pops.<FunctionDefinition>lambdaQuery().from(FunctionDefinition.MODEL_MODEL)
                .eq(FunctionDefinition::getNamespace, nameSpace)
                .eq(FunctionDefinition::getFun, fun);
        List<FunctionDefinition> list = new FunctionDefinition().queryList(wrapper);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public static <T extends K2> List<T> fullDefault(List<T> list) {
        Optional.ofNullable(list).filter(CollectionUtils::isNotEmpty).ifPresent(_ls -> {
            for (T t : list) {
                t.construct();
            }
        });
        return list;
    }

    public <R> void fullPreparer(EipInterfaceEditPrepareChain<R> chain) {
        List<EipInterfaceEditPreparer> beansOfTypeByOrdered = BeanDefinitionUtils.getBeansOfTypeByOrdered(EipInterfaceEditPreparer.class);
        Collections.reverse(beansOfTypeByOrdered);
        for (EipInterfaceEditPreparer value : beansOfTypeByOrdered) {
            chain.addPreparer(value);
        }
    }

}
