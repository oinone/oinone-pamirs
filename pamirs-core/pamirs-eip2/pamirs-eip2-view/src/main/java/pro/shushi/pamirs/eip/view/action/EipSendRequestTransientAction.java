package pro.shushi.pamirs.eip.view.action;

import pro.shushi.pamirs.locale.utils.I18nUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipParamMapping;
import pro.shushi.pamirs.eip.api.service.model.EipSendRequestTransientService;
import pro.shushi.pamirs.eip.api.tmodel.EipSendRequestTransient;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * @author yeshenyue on 2024/8/21 14:02.
 */
@Slf4j
@Component
@Model.model(EipSendRequestTransient.MODEL_MODEL)
public class EipSendRequestTransientAction {

    @Autowired
    private EipSendRequestTransientService eipSendRequestTransientService;

    @Function.Advanced(type = FunctionTypeEnum.UPDATE)
    @Function.fun(EipFunctionConstant.EIP_SEND_REQUEST_FUN)
    @Function(openLevel = {LOCAL, REMOTE, API})
    @Action(displayName = "发送请求", contextType = ActionContextTypeEnum.SINGLE)
    public EipSendRequestTransient sendRequest(EipSendRequestTransient data) {
        EipIntegrationInterface eipInterface = fetchIntegrationInterface(data);
        EipParamMapping eipParamMapping = fetchEipParamMapping(data);
        EipSendRequestTransient result = eipSendRequestTransientService
                .sendEipRequest(eipInterface, eipParamMapping, data.getRequestData(), data.getModel());
        data.setResponseData(result.getResponseData());
        PamirsSession.getMessageHub().success(I18nUtils.getMessage("pamirs-eip2-view.EipSendRequestTransientAction.success"));
        return data;
    }

    private EipParamMapping fetchEipParamMapping(EipSendRequestTransient data) {
        if (data == null || StringUtils.isBlank(data.getInterfaceName())
                || StringUtils.isBlank(data.getActionName())
                || StringUtils.isBlank(data.getModel())
                || StringUtils.isBlank(data.getViewName())) {
            throw PamirsException.construct(EipExpEnumerate.EIP_SEND_REQUEST_PARAM_NULL).errThrow();
        }

        EipParamMapping eipParamMapping = new EipParamMapping();
        eipParamMapping.setInterfaceName(data.getInterfaceName());
        eipParamMapping.setViewName(data.getViewName());
        eipParamMapping.setActionName(data.getActionName());
        eipParamMapping = eipParamMapping.queryOne();
        if (eipParamMapping == null) {
            log.error("Technical name:{}, model code:{}, action name:{}, view name:{}",
                    data.getInterfaceName(), data.getModel(), data.getActionName(), data.getViewName());
            throw PamirsException.construct(EipExpEnumerate.EIP_PARAM_MAPPING_IS_NULL).errThrow();
        }
        return eipParamMapping;
    }

    private EipIntegrationInterface fetchIntegrationInterface(EipSendRequestTransient data) {
        if (data == null || StringUtils.isBlank(data.getInterfaceName())) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
        }
        EipIntegrationInterface anInterface = new EipIntegrationInterface()
                .setInterfaceName(data.getInterfaceName()).queryOne();
        if (anInterface == null) {
            throw PamirsException.construct(EipExpEnumerate.INTEGRATION_INTERFACE_NULL_ERROR).errThrow();
        }
        return anInterface;
    }
}
