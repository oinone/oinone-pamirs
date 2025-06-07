package pro.shushi.pamirs.eip.view.manager.edit.preparer;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.ParamProcessorTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.eip.api.service.edit.EipInterfaceEditConvertService;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;
import pro.shushi.pamirs.eip.view.manager.EipIntegrationInterfaceEditManager;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPrepareChain;
import pro.shushi.pamirs.eip.view.manager.edit.EipInterfaceEditPreparer;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;
import java.util.Optional;


@Component
@Order(3)
@Qualifier("responsePreparer")
public class ResponsePreparer<R> implements EipInterfaceEditPreparer<R>, EipInterfaceEditConvertService<EipParamProcessor> {


    @Override
    public R prepare(EipIntegrationInterfaceEdit interfaceEdit, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterface eipIntegrationInterface = prepareChain.getEipIntegrationInterface();
        eipIntegrationInterface.setResponseParamProcessor(convert(interfaceEdit));

        return prepareChain.prepare(interfaceEdit, prepareChain);
    }

    @Override
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterfaceEdit edit = prepareChain.getEipIntegrationInterfaceEdit();
        EipParamProcessor responseParamProcessor = integrationInterface.getResponseParamProcessor();
        Optional.ofNullable(responseParamProcessor).ifPresent(_pro -> {
            FunctionDefinition inOutFun = EipIntegrationInterfaceEditManager.fetchFun(_pro.getInOutConverterNamespace(), _pro.getInOutConverterFun());
            edit.setRespInOutConverterFunction(inOutFun);
            edit.setRespFinalResultKey(_pro.getFinalResultKey());
            edit.setRespConvertParamList((List<EipConvertParam>) (Object) _pro.getConvertParamList());
        });
        return prepareChain.construct(integrationInterface, prepareChain);
    }

    @Override
    public EipParamProcessor convert(EipIntegrationInterfaceEdit interfaceEdit) {
        EipParamProcessor eipParamProcessor = new EipParamProcessor();
        eipParamProcessor.setType(ParamProcessorTypeEnum.RESPONSE);
        //请求体
        FunctionDefinition respInOutConverterFunction = interfaceEdit.getRespInOutConverterFunction();

        Optional.ofNullable(respInOutConverterFunction).ifPresent(_fun -> {
            String inOutFunNameSpace = _fun.getNamespace();
            String inOutFun = _fun.getFun();
            eipParamProcessor.setInOutConverterNamespace(inOutFunNameSpace);
            eipParamProcessor.setInOutConverterFun(inOutFun);
        });

        //参数key映射
        List<EipConvertParam> respConvertParamList = interfaceEdit.getRespConvertParamList();
        eipParamProcessor.setConvertParamList(EipIntegrationInterfaceEditManager.fullDefault(respConvertParamList));

//        eipParamProcessor.setFinalResultKey(Optional.ofNullable(interfaceEdit.getRespFinalResultKey()).filter(StringUtils::isNotBlank).orElse("empty"));

        eipParamProcessor.setFinalResultKey(interfaceEdit.getRespFinalResultKey());
        return eipParamProcessor;
    }
}
