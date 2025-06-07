package pro.shushi.pamirs.eip.view.manager.edit.preparer;


import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.tmodel.EipContextVariable;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.eip.view.manager.EipIntegrationInterfaceEditManager;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPrepareChain;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPreparer;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;
import java.util.Optional;


@Component
@Order(5)
public class ContextPreparer<R> implements EipInterfaceEditPreparer<R> {

    @Override
    public R prepare(EipIntegrationInterfaceEdit interfaceEdit, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterface eipIntegrationInterface = prepareChain.getEipIntegrationInterface();

        //上下文提供函数
        FunctionDefinition contextSupplierFunction = interfaceEdit.getContextSupplierFunction();
        Optional.ofNullable(contextSupplierFunction).ifPresent(_fun -> {
            String namespace = _fun.getNamespace();
            String fun = _fun.getFun();
            eipIntegrationInterface.setContextSupplierFun(fun);
            eipIntegrationInterface.setContextSupplierNamespace(namespace);
        });

        //上下文key value
        List<EipContextVariable> contextVariableList = interfaceEdit.getContextVariableList();

        return prepareChain.prepare(interfaceEdit, prepareChain);
    }

    @Override
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterfaceEdit edit = prepareChain.getEipIntegrationInterfaceEdit();
        Optional.ofNullable(integrationInterface).ifPresent(_in -> {
            String exceptionPredictFun = _in.getContextSupplierFun();
            String exceptionPredictNamespace = _in.getContextSupplierNamespace();
            FunctionDefinition functionDefinition = EipIntegrationInterfaceEditManager.fetchFun(exceptionPredictNamespace, exceptionPredictFun);
            edit.setContextSupplierFunction(functionDefinition);

        });
        return prepareChain.construct(integrationInterface, prepareChain);
    }
}
