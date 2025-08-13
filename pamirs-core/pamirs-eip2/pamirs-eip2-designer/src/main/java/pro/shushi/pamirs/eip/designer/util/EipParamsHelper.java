package pro.shushi.pamirs.eip.designer.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipConvertParam;
import pro.shushi.pamirs.eip.api.pamirs.DefaultOpenFunctionConverterFunction;
import pro.shushi.pamirs.eip.designer.model.open.EipOpenReqBodyParam;
import pro.shushi.pamirs.eip.designer.model.open.EipOpenReqHeaderParam;
import pro.shushi.pamirs.eip.designer.model.open.EipOpenReqQueryParam;
import pro.shushi.pamirs.eip.designer.model.open.EipOpenRespParam;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

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

}
