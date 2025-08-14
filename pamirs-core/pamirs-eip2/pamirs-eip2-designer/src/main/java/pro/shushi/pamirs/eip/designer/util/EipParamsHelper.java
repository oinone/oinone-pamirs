package pro.shushi.pamirs.eip.designer.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.pamirs.DefaultOpenFunctionConverterFunction;
import pro.shushi.pamirs.eip.designer.model.conn.*;
import pro.shushi.pamirs.eip.designer.model.open.*;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EipParamsHelper {

    public static void buildQueryParams(List<EipConvertParam> requestConvertParams, List<EipOpenReqQueryParam> queryParams) {
        if (CollectionUtils.isEmpty(queryParams)) {
            return;
        }
        for (EipOpenReqQueryParam param : queryParams) {
            requestConvertParams.add(
                    new EipConvertParam()
                            .setInParam(IEipContext.URL_QUERY_PARAMS_KEY + "." + param.getKey())
                            .setOutParam(DefaultOpenFunctionConverterFunction.OPEN_FUNCTION_CONVERTER_ARGS + "." + param.getValueExpr())
                            .setRequired(param.getRequired())
                            .setDefaultValue(StringUtils.isEmpty(param.getDefaultValue()) ? null : param.getDefaultValue())
            );
        }
    }

    public static void buildHeaderParams(List<EipConvertParam> requestConvertParams, List<EipOpenReqHeaderParam> headerParams) {
        if (CollectionUtils.isEmpty(headerParams)) {
            return;
        }
        for (EipOpenReqHeaderParam param : headerParams) {
            requestConvertParams.add(
                    new EipConvertParam()
                            .setOriginContextType(ContextTypeEnum.EXECUTOR)
                            .setTargetContextType(ContextTypeEnum.EXECUTOR)
                            .setInParam(IEipContext.HEADER_PARAMS_KEY + "." + param.getKey())
                            .setOutParam(DefaultOpenFunctionConverterFunction.OPEN_FUNCTION_CONVERTER_ARGS + "." + param.getKey())
                            .setRequired(param.getRequired())
                            .setDefaultValue(StringUtils.isEmpty(param.getDefaultValue()) ? null : param.getDefaultValue())
            );
        }
    }

    public static void buildBodyParams(List<EipConvertParam> requestConvertParams, List<EipOpenReqBodyParam> bodyParams, String prefix) {
        if (CollectionUtils.isEmpty(bodyParams)) {
            return;
        }
        for (EipOpenReqBodyParam bodyParam : bodyParams) {
            Boolean isMulti = Optional.ofNullable(bodyParam.getIsMulti()).orElse(Boolean.FALSE);
            String key = bodyParam.getKey();
            if (StringUtils.isNotBlank(prefix)) {
                key = prefix + CharacterConstants.SEPARATOR_DOT + key;
            }
            if (isMulti) {
                key = key + IEipContext.DEFAULT_LIST_FLAG_KEY;
            }
            if (CollectionUtils.isEmpty(bodyParam.getChildren())) {
                requestConvertParams.add(
                        new EipConvertParam()
                                .setInParam(key)
                                .setOutParam(DefaultOpenFunctionConverterFunction.OPEN_FUNCTION_CONVERTER_ARGS + CharacterConstants.SEPARATOR_DOT + bodyParam.getValueExpr())
                                .setOutParamType(bodyParam.getParamType())
                                .setRequired(bodyParam.getRequired())
                                .setSize(bodyParam.getSize())
                                .setDefaultValue(StringUtils.isEmpty(bodyParam.getDefaultValue()) ? null : bodyParam.getDefaultValue())
                );
            } else {
                // 有子节点,拼接前缀继续递归. 当前节点不写入转换列表
                buildBodyParams(requestConvertParams, bodyParam.getChildren(), key);
            }
        }
    }

    public static void buildRespParams(List<EipConvertParam> responseConvertParams, List<EipOpenRespParam> respParams, String prefix) {
        if (CollectionUtils.isEmpty(respParams)) {
            return;
        }
        for (EipOpenRespParam param : respParams) {
            Boolean isMulti = Optional.ofNullable(param.getIsMulti()).orElse(Boolean.FALSE);
            String key = param.getKey();
            if (StringUtils.isNotBlank(prefix)) {
                key = prefix + CharacterConstants.SEPARATOR_DOT + key;
            }
            if (isMulti) {
                key = key + IEipContext.DEFAULT_LIST_FLAG_KEY;
            }
            if (CollectionUtils.isEmpty(param.getChildren())) {
                responseConvertParams.add(
                        new EipConvertParam()
                                .setInParam(DefaultOpenFunctionConverterFunction.OPEN_FUNCTION_CONVERTER_RETURN + CharacterConstants.SEPARATOR_DOT + param.getValueExpr())
                                .setOutParam(key)
                );
            } else {
                // 有子节点,拼接前缀继续递归. 当前节点不写入转换列表
                buildRespParams(responseConvertParams, param.getChildren(), key);
            }
        }
    }

    public static List<EipOpenReqQueryParam> buildQueryParams(List<EipReqQueryParam> queryParams) {
        List<EipOpenReqQueryParam> openApiQueryParams = new ArrayList<>(queryParams.size());
        for (EipReqQueryParam queryParam : queryParams) {
            EipOpenReqQueryParam openApiQueryParam = new EipOpenReqQueryParam();
            openApiQueryParam.setKey(queryParam.getKey());
            openApiQueryParam.setRequired(queryParam.getRequired());
            openApiQueryParam.setDefaultValue(queryParam.getDefaultValue());
            openApiQueryParam.setDesc(queryParam.getDesc());
            openApiQueryParam.setValueExpr(queryParam.getKey());
            openApiQueryParam.setParamType(queryParam.getParamType());
            openApiQueryParams.add(openApiQueryParam);
        }
        return openApiQueryParams;
    }

    public static List<EipOpenReqPathParam> buildPathParams(List<EipReqPathParam> pathParams) {
        List<EipOpenReqPathParam> openApiPathParams = new ArrayList<>(pathParams.size());
        for (EipReqPathParam pathParam : pathParams) {
            EipOpenReqPathParam openApiPathParam = new EipOpenReqPathParam();
            openApiPathParam.setKey(pathParam.getKey());
            openApiPathParam.setRequired(pathParam.getRequired());
            openApiPathParam.setDefaultValue(pathParam.getDefaultValue());
            openApiPathParam.setDesc(pathParam.getDesc());
            openApiPathParam.setParamType(pathParam.getParamType());
            openApiPathParams.add(openApiPathParam);
        }
        return openApiPathParams;
    }

    public static List<EipOpenReqHeaderParam> buildHeaderParams(List<EipReqHeaderParam> headerParams) {
        List<EipOpenReqHeaderParam> openApiHeaderParams = new ArrayList<>(headerParams.size());
        for (EipReqHeaderParam headerParam : headerParams) {
            EipOpenReqHeaderParam openApiHeaderParam = new EipOpenReqHeaderParam();
            openApiHeaderParam.setKey(headerParam.getKey());
            openApiHeaderParam.setRequired(headerParam.getRequired());
            openApiHeaderParam.setDefaultValue(headerParam.getDefaultValue());
            openApiHeaderParam.setDesc(headerParam.getDesc());
            openApiHeaderParam.setParamType(headerParam.getParamType());
            openApiHeaderParams.add(openApiHeaderParam);
        }
        return openApiHeaderParams;
    }

    public static List<EipOpenReqBodyParam> buildBodyParams(List<EipReqBodyParam> bodyParams) {
        return buildBodyParams(bodyParams, null);
    }

    public static List<EipOpenReqBodyParam> buildBodyParams(List<EipReqBodyParam> bodyParams, String valueExprPrefix) {
        List<EipOpenReqBodyParam> openApiBodyParams = new ArrayList<>(bodyParams.size());
        for (EipReqBodyParam bodyParam : bodyParams) {
            Boolean isMulti = Optional.ofNullable(bodyParam.getIsMulti()).orElse(Boolean.FALSE);
            String valueExpr = bodyParam.getKey();
            if (StringUtils.isNotBlank(valueExprPrefix)) {
                valueExpr = valueExprPrefix + CharacterConstants.SEPARATOR_DOT + valueExpr;
            }
            if (isMulti) {
                valueExpr = valueExpr + IEipContext.DEFAULT_LIST_FLAG_KEY;
            }
            EipOpenReqBodyParam openApiBodyParam = new EipOpenReqBodyParam();
            openApiBodyParam.setKey(bodyParam.getKey());
            openApiBodyParam.setRequired(bodyParam.getRequired());
            openApiBodyParam.setDefaultValue(bodyParam.getDefaultValue());
            openApiBodyParam.setDesc(bodyParam.getDesc());
            openApiBodyParam.setParamType(bodyParam.getParamType());
            openApiBodyParam.setSize(bodyParam.getSize());
            openApiBodyParam.setIsMulti(isMulti);
            openApiBodyParam.setValueExpr(valueExpr);
            List<EipReqBodyParam> children = bodyParam.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                openApiBodyParam.setChildren(buildBodyParams(children, valueExpr));
            }
            openApiBodyParams.add(openApiBodyParam);
        }
        return openApiBodyParams;
    }

    public static List<EipOpenRespParam> buildResponseParams(List<EipApiResponseParam> responseParams) {
        return buildResponseParams(responseParams, null);
    }

    public static List<EipOpenRespParam> buildResponseParams(List<EipApiResponseParam> responseParams, String valueExprPrefix) {
        List<EipOpenRespParam> openApiResponseParams = new ArrayList<>(responseParams.size());
        for (EipApiResponseParam responseParam : responseParams) {
            String valueExpr = responseParam.getKey();
            if (StringUtils.isNotBlank(valueExprPrefix)) {
                valueExpr = valueExprPrefix + CharacterConstants.SEPARATOR_DOT + valueExpr;
            }
            EipOpenRespParam openApiResponseParam = new EipOpenRespParam();
            openApiResponseParam.setKey(responseParam.getKey());
            openApiResponseParam.setParamType(responseParam.getParamType());
            openApiResponseParam.setIsMulti(responseParam.getIsMulti());
            openApiResponseParam.setValueExpr(valueExpr);
            openApiResponseParam.setDesc(responseParam.getDesc());
            List<EipApiResponseParam> children = responseParam.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                openApiResponseParam.setChildren(buildResponseParams(children, valueExpr));
            }
            openApiResponseParams.add(openApiResponseParam);
        }
        return openApiResponseParams;
    }

    public static List<EipOpenReqBodyParam> caseOpenQueryParams2BodyParams(List<EipOpenReqQueryParam> queryParams) {
        if (CollectionUtils.isEmpty(queryParams)) {
            return new ArrayList<>();
        }
        List<EipOpenReqBodyParam> openApiBodyParams = new ArrayList<>(queryParams.size());
        for (EipOpenReqQueryParam queryParam : queryParams) {
            EipOpenReqBodyParam openApiBodyParam = new EipOpenReqBodyParam();
            openApiBodyParam.setKey(queryParam.getKey());
            openApiBodyParam.setRequired(queryParam.getRequired());
            openApiBodyParam.setDefaultValue(queryParam.getDefaultValue());
            openApiBodyParam.setDesc(queryParam.getDesc());
            openApiBodyParam.setValueExpr(queryParam.getValueExpr());
            openApiBodyParam.setParamType(queryParam.getParamType());
            openApiBodyParams.add(openApiBodyParam);
        }
        return openApiBodyParams;
    }

    public static List<EipOpenReqBodyParam> caseQueryParams2OpenBodyParams(List<EipReqQueryParam> queryParams) {
        if (CollectionUtils.isEmpty(queryParams)) {
            return new ArrayList<>();
        }
        List<EipOpenReqBodyParam> openApiBodyParams = new ArrayList<>(queryParams.size());
        for (EipReqQueryParam queryParam : queryParams) {
            EipOpenReqBodyParam openApiBodyParam = new EipOpenReqBodyParam();
            openApiBodyParam.setKey(queryParam.getKey());
            openApiBodyParam.setRequired(queryParam.getRequired());
            openApiBodyParam.setDefaultValue(queryParam.getDefaultValue());
            openApiBodyParam.setDesc(queryParam.getDesc());
            openApiBodyParam.setValueExpr(queryParam.getKey());
            openApiBodyParam.setParamType(queryParam.getParamType());
            openApiBodyParams.add(openApiBodyParam);
        }
        return openApiBodyParams;
    }

}
