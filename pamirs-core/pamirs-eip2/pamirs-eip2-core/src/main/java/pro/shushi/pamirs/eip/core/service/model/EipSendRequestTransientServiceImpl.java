package pro.shushi.pamirs.eip.core.service.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.constant.EipCharacterConstant;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipParamMapping;
import pro.shushi.pamirs.eip.api.model.EipParamMappingItem;
import pro.shushi.pamirs.eip.api.model.EipParamProcessor;
import pro.shushi.pamirs.eip.api.service.EipExecuteService;
import pro.shushi.pamirs.eip.api.service.model.EipSendRequestTransientService;
import pro.shushi.pamirs.eip.api.tmodel.EipSendRequestTransient;
import pro.shushi.pamirs.eip.api.util.EipParamConverterHelper;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.RtypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author yeshenyue on 2024/9/6 16:02.
 */
@Slf4j
@Service
@Fun(EipSendRequestTransientService.FUN_NAMESPACE)
public class EipSendRequestTransientServiceImpl implements EipSendRequestTransientService {

    @Resource
    private EipExecuteService<SuperMap> eipExecuteService;

    @Resource
    private GenericMapper genericMapper;

    @Override
    public EipSendRequestTransient sendEipRequest(EipIntegrationInterface eipInterface, EipParamMapping paramMapping, String modelData, String model) {
        // 接口上下文参数
        SuperMap interfaceContext = new SuperMap();
        // 执行器上下文参数
        SuperMap executorContext = new SuperMap();

        if (StringUtils.isBlank(modelData)) {
            // 不构建请求参数，直接发送
            EipResult<SuperMap> result = eipExecuteService.callByInterfaceName(eipInterface.getInterfaceName(), executorContext, interfaceContext);
            return convertResponse(result, paramMapping, null, model);
        }

        Boolean isDb = paramMapping.getIsDb();
        Map<String, Object> modelDataMap = JsonUtils.parseMap(modelData);

        for (EipParamMappingItem headerMapping : Optional.ofNullable(paramMapping.getHeaderMapping()).orElse(Collections.emptyList())) {
            Object value = fetchValue(headerMapping.getTo(), modelDataMap);
            if (ObjectUtils.isNotEmpty(value)) {
                executorContext.putIteration(headerMapping.getKey(), value);
            }
        }
        for (EipParamMappingItem queryMapping : Optional.ofNullable(paramMapping.getQueryMapping()).orElse(Collections.emptyList())) {
            Object value = fetchValue(queryMapping.getTo(), modelDataMap);
            if (ObjectUtils.isNotEmpty(value)) {
                if (isDb) {
                    interfaceContext.putIteration(queryMapping.getKey(), value);
                } else {
                    interfaceContext.putIteration(queryMapping.getKey(), value);
                }
            }
        }
        for (EipParamMappingItem pathMapping : Optional.ofNullable(paramMapping.getPathMapping()).orElse(Collections.emptyList())) {
            Object value = fetchValue(pathMapping.getTo(), modelDataMap);
            if (ObjectUtils.isNotEmpty(value)) {
                executorContext.putIteration(pathMapping.getKey(), value);
            }
        }

        String bodyFinalResultKeyPrefix = Optional.ofNullable(eipInterface.getRequestParamProcessor())
                .map(EipParamProcessor::getFinalResultKey).filter(StringUtils::isNotBlank)
                .map(v -> v + EipCharacterConstant.PARAMETER_PARAMETER_SEPARATOR).orElse("");

        for (EipParamMappingItem bodyMapping : Optional.ofNullable(paramMapping.getBodyMapping()).orElse(Collections.emptyList())) {
            Object value = fetchValue(bodyMapping.getTo(), modelDataMap);
            if (ObjectUtils.isNotEmpty(value)) {
                interfaceContext.putIteration(bodyFinalResultKeyPrefix + bodyMapping.getKey(), value);
            }
        }
        EipResult<SuperMap> result = eipExecuteService.callByInterfaceName(eipInterface.getInterfaceName(), executorContext, interfaceContext);
        return convertResponse(result, paramMapping, new SuperMap(modelDataMap), model);
    }

    private EipSendRequestTransient convertResponse(EipResult<SuperMap> callResult, EipParamMapping paramMapping, SuperMap modelDataMap, String model) {
        if (!callResult.getSuccess()) {
            throw PamirsException.construct(EipExpEnumerate.EIP_SEND_REQUEST_ERROR)
                    .appendMsg(callResult.getErrorMessage()).errThrow();
        }

        // 没有配置响应参数映射规则，数据原样返回
        if (CollectionUtils.isEmpty(paramMapping.getResponseMapping())) {
            return new EipSendRequestTransient().setResponseData(modelDataMap);
        }

        if (modelDataMap == null) {
            modelDataMap = new SuperMap();
        }

        // Eip返回的数据，转换成JSON格式
        JSON eipResult = fetchResultJson(callResult.getResult());
        if (paramMapping.getIsDb()) {
            eipResult = new JSONObject(Collections.singletonMap(IEipContext.EIP_SQL_RESULT_IN, eipResult));
        }

        // 清空待映射数据
        clearOldModelDataMap(modelDataMap, paramMapping.getResponseMapping());

        for (EipParamMappingItem responseMapping : paramMapping.getResponseMapping()) {
            if (StringUtils.isBlank(responseMapping.getValueExpr())) {
                log.error("响应参数映射失败，取值表达式为空，from:{}，to:{}", responseMapping.getKey(), responseMapping.getTo());
                throw PamirsException.construct(EipExpEnumerate.EIP_RESPONSE_VALUE_EXP_IS_NULL).errThrow();
            }
            // 根据表达式获取接口返回的数据
            Object resultItem = EipParamConverterHelper.extractData(eipResult, responseMapping.getValueExpr());

            // 映射的模型字段名称
            String targetPath = responseMapping.getTo();

            if (resultItem == null) {
                modelDataMap.remove(targetPath);
            } else if (resultItem instanceof List) {
                handleListConversion(model, targetPath, modelDataMap, FetchUtil.cast(resultItem));
            } else {
                modelDataMap.putIteration(targetPath, resultItem);
            }
        }

        handlerRelationFields(modelDataMap, model);
        Object result = handlerBaseTypeFields(modelDataMap, model);
        return new EipSendRequestTransient().setResponseData(result);
    }

    private Object handlerBaseTypeFields(SuperMap modelDataMap, String model) {
        ClientDataConverter clientDataConverter = ClientDataConverter.get();
        Object data = clientDataConverter.in(new ModelComputeContext(), model, modelDataMap);
        Map<?, ?> _data = null;
        if (data instanceof D) {
            _data = ((D) data).get_d();
        } else if (data instanceof Map) {
            _data = (Map<?, ?>) data;
        }
        try {
            return clientDataConverter.out(model, _data);
        } catch (Exception e) {
            throw PamirsException.construct(EipExpEnumerate.EIP_RESPONSE_CONVERT_ERROR).errThrow();
        }
    }

    /**
     * 处理关系字段，并删除无法识别的基础数据类型字段数据
     */
    private void handlerRelationFields(SuperMap modelDataMap, String model) {
        if (MapUtils.isEmpty(modelDataMap)) {
            return;
        }

        // 获取有值的关系字段
        List<String> relationFieldKeys = new ArrayList<>();
        for (Map.Entry<String, Object> entry : modelDataMap.entrySet()) {
            Object value = entry.getValue();
            if (ObjectUtils.isNotEmpty(value) && (value instanceof List || value instanceof Map)) {
                relationFieldKeys.add(entry.getKey());
            }
        }

        for (String fieldName : relationFieldKeys) {
            ModelFieldConfig modelFieldConfig = PamirsSession.getContext().getModelField(model, fieldName);
            if (modelFieldConfig == null) {
                log.error("获取字段信息失败，模型编码：{}，字段名称：{}", model, fieldName);
                continue;
            }

            Object relationData = modelDataMap.get(fieldName);
            if (RtypeEnum.isRelationOne(modelFieldConfig.getTtype())) {
                if (!(relationData instanceof Map)) {
                    log.error("接口返回数据与模型关系字段映射失败，应为Map类型，实际：{}，字段类型：{}，字段名称：{}",
                            relationData.getClass().getSimpleName(), modelFieldConfig.getTtype(), fieldName);
                    modelDataMap.remove(fieldName);
                    continue;
                }

                Map<String, Object> data = FetchUtil.cast(relationData);
                DataMap dataMap = new DataMap(data).setModel(modelFieldConfig.getReferences());
                try {
                    DataMap dbDataMap = genericMapper.selectOneByEntity(dataMap);
                    if (MapUtils.isEmpty(dbDataMap)) {
                        log.warn("映射{}关系字段数据失败，数据库中不存在此数据，模型编码：{}，字段名称：{}，查询条件：{}",
                                modelFieldConfig.getTtype(), modelFieldConfig.getModel(), fieldName, JsonUtils.toJSONString(dataMap));
                    } else {
                        modelDataMap.put(fieldName, dbDataMap);
                    }
                } catch (Exception e) {
                    log.error("查询M2O/O2O关系字段数据失败，字段名称：{}，查询条件：{}", fieldName, JsonUtils.toJSONString(dataMap), e);
                }
            } else if (TtypeEnum.isBasicType(modelFieldConfig.getTtype())) {
                if (relationData instanceof List) {
                    List<Object> dataList = FetchUtil.cast(relationData);
                    if (dataList.size() == 1) {
                        modelDataMap.put(fieldName, dataList.get(0));
                        continue;
                    }
                }
                modelDataMap.remove(fieldName);
                log.error("映射失败，未能识别的数据，字段名称:{}，数据：{}", fieldName, JsonUtils.toJSONString(relationData));
            }
        }
    }

    /**
     * 处理列表类型的数据，根据映射的模型字段名称表达式将数据映射至模型数据中
     *
     * @param model        模型名称
     * @param targetPath   映射的模型字段名称表达式
     * @param modelDataMap 模型数据
     * @param resultList   处理后的接口返回数据
     */
    private void handleListConversion(String model, String targetPath, SuperMap modelDataMap, List<?> resultList) {
        if (!targetPath.contains(".")) {
            // 表达式无层级关系，直接填入数据
            modelDataMap.put(targetPath, resultList);
            return;
        }

        String[] pathSegments = targetPath.split("\\.");

        // 表达式中每一层的字段配置
        List<ModelFieldConfig> fieldConfigList = new ArrayList<>(pathSegments.length);

        // 获取第一层字段信息
        ModelFieldConfig rootFieldConfig = PamirsSession.getContext().getModelField(model, pathSegments[0]);
        fieldConfigList.add(rootFieldConfig);
        int multiLevel = rootFieldConfig.getMulti() ? 0 : -1;

        // 获取表达式中每一层的字段配置，并记录最远的多值字段位置
        for (int i = 1; i < pathSegments.length; i++) {
            String referencedModel = fieldConfigList.get(i - 1).getReferences();
            ModelFieldConfig fieldConfig = PamirsSession.getContext().getModelField(referencedModel, pathSegments[i]);
            fieldConfigList.add(fieldConfig);
            if (fieldConfig.getMulti()) {
                multiLevel = i;
            }
        }

        // 没有找到多值字段
        if (multiLevel == -1) {
            if (resultList.size() > 1) {
                // 所有字段都不是多值且返回数据为多个，无法映射列表
                log.error("响应参数映射失败，表达式：{}，响应参数：{}", targetPath, JsonUtils.toJSONString(resultList));
                throw PamirsException.construct(EipExpEnumerate.EIP_RESPONSE_PARAM_NO_MATCH).errThrow();
            } else {
                modelDataMap.put(targetPath, resultList.get(0));
                return;
            }
        }

        // 表达式最后一位为多值
        if (multiLevel == pathSegments.length - 1) {
            modelDataMap.put(pathSegments[multiLevel], resultList);
            return;
        }

        // 多值字段的表达式（多值字段为关系字段）
        String prefix = Joiner.on(".").skipNulls().join(Arrays.asList(pathSegments).subList(0, multiLevel + 1));
        // 多值关系字段中的字段名称
        String keyName = Joiner.on(".").skipNulls().join(Arrays.asList(pathSegments).subList(multiLevel + 1, pathSegments.length));

        Object targetFieldData = modelDataMap.get(prefix);
        if (targetFieldData != null && !(targetFieldData instanceof List)) {
            log.error("尝试获取List类型数据失败，参数信息：{}", JsonUtils.toJSONString(targetFieldData));
            throw PamirsException.construct(EipExpEnumerate.EIP_RESPONSE_PARAM_NO_MATCH).errThrow();
        }

        // 获取模型数据中原有的值
        List<Object> existingDataList = targetFieldData == null ?
                new ArrayList<>() : (List<Object>) modelDataMap.get(prefix);

        List<Map<String, Object>> convertedList = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            Map<String, Object> item;
            if (i < existingDataList.size()) {
                Object data = existingDataList.get(i);
                if (data instanceof Map) {
                    item = FetchUtil.cast(data);
                } else {
                    log.error("响应参数映射失败，数据类型：{}，数据：{}", data.getClass().getSimpleName(), JsonUtils.toJSONString(data));
                    throw PamirsException.construct(EipExpEnumerate.EIP_RESPONSE_PARAM_NO_MATCH).errThrow();
                }
            } else {
                item = new HashMap<>();
            }
            MapHelper.putIteration(item, keyName, resultList.get(i));
            convertedList.add(item);
        }

        modelDataMap.put(prefix, convertedList);
    }

    private JSON fetchResultJson(String result) {
        try (JSONValidator validator = JSONValidator.from(result)) {
            return JSONValidator.Type.Array.equals(validator.getType()) ? JSON.parseArray(result) : JSON.parseObject(result);
        } catch (Exception e) {
            log.error("JSON数据解析失败", e);
            throw PamirsException.construct(EipExpEnumerate.EIP_JSON_PARSING_ERROR).errThrow();
        }
    }

    private Object fetchValue(String expression, Map<String, Object> context) {
        try {
            return Exp.run(expression, context);
        } catch (Exception e) {
            log.error("表达式识别失败，表达式：{}，数据：{}", expression, JsonUtils.toJSONString(context), e);
            throw PamirsException.construct(EipExpEnumerate.EIP_REQUEST_EXPRESSION_ERROR).appendMsg(expression).errThrow();
        }
    }

    private void clearOldModelDataMap(SuperMap modelDataMap, List<EipParamMappingItem> responseMapping) {
        for (EipParamMappingItem mappingItem : responseMapping) {
            String[] tos = mappingItem.getTo().split("\\.");
            modelDataMap.remove(tos[0]);
        }
    }
}
