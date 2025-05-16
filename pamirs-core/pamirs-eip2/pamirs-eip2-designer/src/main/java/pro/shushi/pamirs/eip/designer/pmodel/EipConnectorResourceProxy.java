package pro.shushi.pamirs.eip.designer.pmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.designer.model.conn.*;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorResource;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * EIP连接器资源扩展
 *
 * @author Adamancy Zhang at 14:59 on 2025-02-26
 */
@Data
public class EipConnectorResourceProxy extends EipConnectorResource {

    private static final long serialVersionUID = 613090202447054561L;

    public EipConnectorResourceProxy() {
        super();
        Models.api().setModel(this, EipConnectorResource.MODEL_MODEL);
        Models.api().setDataModel(EipConnectorResource.MODEL_MODEL, this);
    }

    public EipConnectorResourceProxy(EipConnectorResource resource) {
        super();
        set_d(resource.get_d());
        if (resource instanceof EipConnectorResourceProxy) {
            EipConnectorResourceProxy origin = (EipConnectorResourceProxy) resource;
            setQueryParams(origin.getQueryParams());
            setPathParams(origin.getPathParams());
            setHeaderParams(origin.getHeaderParams());
            setBodyParams(origin.getBodyParams());
            setResponse(origin.getResponse());
        }
    }

    private List<EipReqQueryParam> queryParams;

    private List<EipReqPathParam> pathParams;

    private List<EipReqHeaderParam> headerParams;

    private List<EipReqBodyParam> bodyParams;

    private List<EipApiResponseParam> response;

    private static final TypeReference<List<EipReqQueryParam>> QUERY_TR = new TypeReference<List<EipReqQueryParam>>() {
    };

    private static final TypeReference<List<EipReqPathParam>> PATH_TR = new TypeReference<List<EipReqPathParam>>() {
    };

    private static final TypeReference<List<EipReqHeaderParam>> HEADER_TR = new TypeReference<List<EipReqHeaderParam>>() {
    };

    private static final TypeReference<List<EipReqBodyParam>> BODY_TR = new TypeReference<List<EipReqBodyParam>>() {
    };

    private static final TypeReference<List<EipApiResponseParam>> RESP_TR = new TypeReference<List<EipApiResponseParam>>() {
    };

    public List<EipReqQueryParam> parseQueryParams() {
        List<EipReqQueryParam> queryParams = getQueryParams();
        if (queryParams != null) {
            return queryParams;
        }
        queryParams = Optional.ofNullable(getQueryParamsJson())
                .filter(StringUtils::isNotBlank)
                .filter(JSON::isValidArray)
                .map(json -> JsonUtils.parseObject(json, QUERY_TR))
                .orElse(Collections.emptyList());
        setQueryParams(queryParams);
        return queryParams;
    }

    public List<EipReqPathParam> parsePathParams() {
        List<EipReqPathParam> pathParams = getPathParams();
        if (pathParams != null) {
            return pathParams;
        }
        pathParams = Optional.ofNullable(getPathParamsJson())
                .filter(StringUtils::isNotBlank)
                .filter(JSON::isValidArray)
                .map(json -> JsonUtils.parseObject(json, PATH_TR))
                .orElse(Collections.emptyList());
        setPathParams(pathParams);
        return pathParams;
    }

    public List<EipReqHeaderParam> parseHeaderParams() {
        List<EipReqHeaderParam> headerParams = getHeaderParams();
        if (headerParams != null) {
            return headerParams;
        }
        headerParams = Optional.ofNullable(getHeaderParamsJson())
                .filter(StringUtils::isNotBlank)
                .filter(JSON::isValidArray)
                .map(json -> JsonUtils.parseObject(json, HEADER_TR))
                .orElse(Collections.emptyList());
        setHeaderParams(headerParams);
        return headerParams;
    }

    public List<EipReqBodyParam> parseBodyParams() {
        List<EipReqBodyParam> bodyParams = getBodyParams();
        if (bodyParams != null) {
            return bodyParams;
        }
        bodyParams = Optional.ofNullable(getBodyParamsJson())
                .filter(StringUtils::isNotBlank)
                .filter(JSON::isValidArray)
                .map(json -> JsonUtils.parseObject(json, BODY_TR))
                .orElse(Collections.emptyList());
        setBodyParams(bodyParams);
        return bodyParams;
    }

    public List<EipApiResponseParam> parseResponse() {
        List<EipApiResponseParam> response = getResponse();
        if (response != null) {
            return response;
        }
        response = Optional.ofNullable(getResponseJson())
                .filter(StringUtils::isNotBlank)
                .filter(JSON::isValidArray)
                .map(json -> JsonUtils.parseObject(json, RESP_TR))
                .orElse(Collections.emptyList());
        setResponse(response);
        return response;
    }
}
