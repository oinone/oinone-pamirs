package pro.shushi.pamirs.eip.designer.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.designer.model.conn.*;
import pro.shushi.pamirs.eip.designer.pmodel.EipConnectorResourceProxy;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorResource;
import pro.shushi.pamirs.meta.util.JsonUtils;

/**
 * EIP转换帮助类
 *
 * @author Adamancy Zhang at 21:41 on 2025-02-26
 */
public class EipConvertHelper {

    public static EipConnectorResourceProxy of(EipConnectorResource eipConnectorResource) {
        if (eipConnectorResource == null) {
            return null;
        }
        EipConnectorResourceProxy result = new EipConnectorResourceProxy();
        result.setConnectorId(eipConnectorResource.getConnectorId());
        result.setInterfaceName(eipConnectorResource.getInterfaceName());
        result.setName(eipConnectorResource.getName());
        result.setSql(eipConnectorResource.getSql());
        result.setDesc(eipConnectorResource.getDesc());
        result.setPreview(eipConnectorResource.getPreview());
        result.setIsSingleResultSet(eipConnectorResource.getIsSingleResultSet());

        if (StringUtils.isNotBlank(eipConnectorResource.getQueryParamsJson())) {
            result.setQueryParams(JsonUtils.parseObjectList(eipConnectorResource.getQueryParamsJson(), EipReqQueryParam.class));
        }
        if (StringUtils.isNotBlank(eipConnectorResource.getPathParamsJson())) {
            result.setPathParams(JsonUtils.parseObjectList(eipConnectorResource.getPathParamsJson(), EipReqPathParam.class));
        }
        if (StringUtils.isNotBlank(eipConnectorResource.getHeaderParamsJson())) {
            result.setHeaderParams(JsonUtils.parseObjectList(eipConnectorResource.getHeaderParamsJson(), EipReqHeaderParam.class));
        }
        if (StringUtils.isNotBlank(eipConnectorResource.getBodyParamsJson())) {
            result.setBodyParams(JsonUtils.parseObjectList(eipConnectorResource.getBodyParamsJson(), EipReqBodyParam.class));
        }
        if (StringUtils.isNotBlank(eipConnectorResource.getResponseJson())) {
            result.setResponse(JsonUtils.parseObjectList(eipConnectorResource.getResponseJson(), EipApiResponseParam.class));
        }
        return result;
    }
}
