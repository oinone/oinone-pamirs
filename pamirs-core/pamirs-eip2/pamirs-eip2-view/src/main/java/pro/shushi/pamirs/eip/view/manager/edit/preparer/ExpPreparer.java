package pro.shushi.pamirs.eip.view.manager.edit.preparer;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.model.EipExceptionParamProcessor;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.service.edit.EipInterfaceEditConvertService;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.eip.view.manager.EipIntegrationInterfaceEditManager;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPrepareChain;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPreparer;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;
import java.util.Optional;


@Component
@Order(4)
@Qualifier("expPreparer")
public class ExpPreparer<R> implements EipInterfaceEditPreparer<R>, EipInterfaceEditConvertService<EipExceptionParamProcessor> {

    @Override
    public R prepare(EipIntegrationInterfaceEdit interfaceEdit, EipInterfaceEditPrepareChain<R> prepareChain) {

        EipIntegrationInterface eipIntegrationInterface = prepareChain.getEipIntegrationInterface();
        eipIntegrationInterface.setExceptionParamProcessor(convert(interfaceEdit));

        return prepareChain.prepare(interfaceEdit, prepareChain);
    }

    @Override
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterfaceEdit edit = prepareChain.getEipIntegrationInterfaceEdit();
        EipExceptionParamProcessor exceptionParamProcessor = integrationInterface.getExceptionParamProcessor();
        Optional.ofNullable(exceptionParamProcessor).ifPresent(_pro -> {
            String exceptionPredictFun = exceptionParamProcessor.getExceptionPredictFun();
            String exceptionPredictNamespace = exceptionParamProcessor.getExceptionPredictNamespace();
            FunctionDefinition functionDefinition = EipIntegrationInterfaceEditManager.fetchFun(exceptionPredictNamespace, exceptionPredictFun);
            edit.setExceptionPredictFunction(functionDefinition);
            edit.setExpConvertParamList((List<EipConvertParam>) (Object) _pro.getConvertParamList());

        });
        return prepareChain.construct(integrationInterface, prepareChain);
    }

    @Override
    public EipExceptionParamProcessor convert(EipIntegrationInterfaceEdit interfaceEdit) {
        EipExceptionParamProcessor eipParamProcessor = new EipExceptionParamProcessor();

        FunctionDefinition exceptionPredictFunction = interfaceEdit.getExceptionPredictFunction();
        Optional.ofNullable(exceptionPredictFunction).ifPresent(_fun -> {
            String namespace = _fun.getNamespace();
            String fun = _fun.getFun();
            eipParamProcessor.setExceptionPredictNamespace(namespace);
            eipParamProcessor.setExceptionPredictFun(fun);
        });

        //参数key映射
        List<EipConvertParam> expConvertParamList = interfaceEdit.getExpConvertParamList();
        eipParamProcessor.setConvertParamList(EipIntegrationInterfaceEditManager.fullDefault(expConvertParamList));
        return eipParamProcessor;
    }
}
