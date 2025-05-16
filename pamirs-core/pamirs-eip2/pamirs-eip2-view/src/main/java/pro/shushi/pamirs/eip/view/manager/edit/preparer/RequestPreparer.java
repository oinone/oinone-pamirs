package pro.shushi.pamirs.eip.view.manager.edit.preparer;

import org.apache.camel.Exchange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.enmu.HttpMethodEnum;
import pro.shushi.pamirs.eip.api.enmu.HttpParamTypeEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@Order(2)
@Primary
@Qualifier("requestPreparer")
public class RequestPreparer<R> implements EipInterfaceEditPreparer<R> , EipInterfaceEditConvertService<EipParamProcessor>{

    private final String uriParamPre = IEipContext.URL_QUERY_PARAMS_KEY+".";
    private final String headerParamPre = IEipContext.HEADER_PARAMS_KEY+".";
    private final String httpMethod = IEipContext.HEADER_PARAMS_KEY + "." + Exchange.HTTP_METHOD;


    @Override
    public R prepare(EipIntegrationInterfaceEdit interfaceEdit, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterface eipIntegrationInterface = prepareChain.getEipIntegrationInterface();
        eipIntegrationInterface.setRequestParamProcessor(convert(interfaceEdit));
        return prepareChain.prepare(interfaceEdit,prepareChain);
    }

    @Override
    public R construct(EipIntegrationInterface integrationInterface, EipInterfaceEditPrepareChain<R> prepareChain) {
        EipIntegrationInterfaceEdit edit = prepareChain.getEipIntegrationInterfaceEdit();
        EipParamProcessor requestParamProcessor = integrationInterface.getRequestParamProcessor();
        Optional.ofNullable(requestParamProcessor).ifPresent(_pro -> {
            FunctionDefinition inOutFun = EipIntegrationInterfaceEditManager.fetchFun(_pro.getInOutConverterNamespace(), _pro.getInOutConverterFun());
            edit.setReqInOutConverterFunction(inOutFun);
            FunctionDefinition paramFun = EipIntegrationInterfaceEditManager.fetchFun(_pro.getParamConverterNamespace(), _pro.getParamConverterFun());
            edit.setParamConverterFunction(paramFun);
            FunctionDefinition authFun = EipIntegrationInterfaceEditManager.fetchFun(_pro.getAuthenticationProcessorNamespace(), _pro.getAuthenticationProcessorFun());
            edit.setReqAuthenticationProcessorFunction(authFun);
            List<EipConvertParam> uriConvertParamList = new ArrayList<>();
            List<EipConvertParam> bodyConvertParamList = new ArrayList<>();
            List<EipConvertParam> headerConvertParamList = new ArrayList<>();
            Optional.ofNullable(requestParamProcessor.getConvertParamList()).filter(CollectionUtils::isNotEmpty).ifPresent(_list -> {
                for (IEipConvertParam<SuperMap> param : _list) {
                    EipConvertParam param1 =  (EipConvertParam)param;
                    String outParam = param1.getOutParam();
                    if (StringUtils.isNotBlank(outParam)) {
                        if (outParam.contains(uriParamPre)) {
                            String[] split = outParam.split(uriParamPre);
                            param1.setOutParam(split[1]);
                            uriConvertParamList.add(param1);
                        } else if (outParam.contains(headerParamPre) && !outParam.contains(httpMethod)) {
                            String[] split = outParam.split(headerParamPre);
                            param1.setOutParam(split[1]);
                            headerConvertParamList.add(param1);
                        } else if (outParam.contains(httpMethod)) {
                            edit.setHttpMethodEnum(HttpMethodEnum.valueOf(param1.getDefaultValue()));
                        } else {
                            bodyConvertParamList.add(param1);
                        }
                    }
                }
                edit.setHeaderConvertParamList(headerConvertParamList);
                edit.setUriConvertParamList(uriConvertParamList);
                edit.setReqConvertParamList(bodyConvertParamList);
            });
            edit.setReqFinalResultKey(_pro.getFinalResultKey());

        });
        return prepareChain.construct(integrationInterface,prepareChain);
    }

    @Override
    public EipParamProcessor convert(EipIntegrationInterfaceEdit interfaceEdit) {
        EipParamProcessor eipParamProcessor = new EipParamProcessor();
        eipParamProcessor.setType(ParamProcessorTypeEnum.REQUEST);
        //认证
        FunctionDefinition reqAuthenticationProcessorFunction = interfaceEdit.getReqAuthenticationProcessorFunction();
        Optional.ofNullable(reqAuthenticationProcessorFunction).ifPresent(_fun -> {
            String namespace = _fun.getNamespace();
            String fun = _fun.getFun();
            eipParamProcessor.setAuthenticationProcessorNamespace(namespace);
            eipParamProcessor.setAuthenticationProcessorFun(fun);
        });

        //参数
        FunctionDefinition paramConverterFunction = interfaceEdit.getParamConverterFunction();
        Optional.ofNullable(paramConverterFunction).ifPresent(_fun -> {
            String paramFunNameSpace = _fun.getNamespace();
            String paramFun = _fun.getNamespace();
            eipParamProcessor.setParamConverterFun(paramFun);
            eipParamProcessor.setParamConverterNamespace(paramFunNameSpace);
        });

        //请求体
        FunctionDefinition converterFunction = interfaceEdit.getConverterFunction();
        Optional.ofNullable(converterFunction).ifPresent(_fun -> {
            String convertFunNamespace = _fun.getNamespace();
            String convertFun = _fun.getFun();
            eipParamProcessor.setConverterFun(convertFun);
            eipParamProcessor.setConverterNamespace(convertFunNamespace);
        });

        //请求体
        FunctionDefinition reqInOutConverterFunction = interfaceEdit.getReqInOutConverterFunction();
        Optional.ofNullable(reqInOutConverterFunction).ifPresent(_fun -> {
            String inOutFunNameSpace = _fun.getNamespace();
            String inOutFun = _fun.getFun();
            eipParamProcessor.setInOutConverterNamespace(inOutFunNameSpace);
            eipParamProcessor.setInOutConverterFun(inOutFun);
        });

        //参数key映射
        List<EipConvertParam> reqConvertParamList = interfaceEdit.getReqConvertParamList();
        Optional.ofNullable(reqConvertParamList).filter(CollectionUtils::isNotEmpty).ifPresent(_ls -> {
            for (EipConvertParam param : _ls) {
                param.setHttpParamTypeEnum(HttpParamTypeEnum.BODY);
            }
        });
        if (null != interfaceEdit.getHttpMethodEnum()) {
            EipConvertParam eipConvertParam = new EipConvertParam();
            eipConvertParam.setOutParam(httpMethod);
            eipConvertParam.setDefaultValue(interfaceEdit.getHttpMethodEnum().getValue());
            eipConvertParam.setInParam(StringUtils.EMPTY);
            eipConvertParam.setHttpParamTypeEnum(HttpParamTypeEnum.METHOD);
            eipConvertParam.construct();
            Optional.ofNullable(reqConvertParamList).ifPresent(_ls -> _ls.add(eipConvertParam));
        }
        eipParamProcessor.setConvertParamList(reqConvertParamList);

        List<EipConvertParam> uriConvertParamList = interfaceEdit.getUriConvertParamList();
        Optional.ofNullable(uriConvertParamList).filter(CollectionUtils::isNotEmpty).ifPresent(_ls -> {
            for (EipConvertParam param : _ls) {
                param.setOutParam(uriParamPre+param.getOutParam());
                param.setHttpParamTypeEnum(HttpParamTypeEnum.PATH);
            }
            Optional.ofNullable(reqConvertParamList).ifPresent(_list -> {
                _list.addAll(_ls);
                EipIntegrationInterfaceEditManager.fullDefault(_list);
            });
        });
        List<EipConvertParam> headerConvertParamList = interfaceEdit.getHeaderConvertParamList();
        Optional.ofNullable(headerConvertParamList).filter(CollectionUtils::isNotEmpty).ifPresent(_ls -> {
            for (EipConvertParam param : _ls) {
                param.setOutParam(headerParamPre+param.getOutParam());
                param.setHttpParamTypeEnum(HttpParamTypeEnum.HEADER);
            }
            Optional.ofNullable(reqConvertParamList).ifPresent(_list -> {
                _list.addAll(_ls);
                EipIntegrationInterfaceEditManager.fullDefault(_list);
            });
        });


//        eipParamProcessor.setFinalResultKey(Optional.ofNullable(interfaceEdit.getReqFinalResultKey()).filter(StringUtils::isNotBlank).orElse("empty"));
        eipParamProcessor.setFinalResultKey(interfaceEdit.getReqFinalResultKey());
        return eipParamProcessor;
    }
}
