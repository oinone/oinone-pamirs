package pro.shushi.pamirs.eip.core.service.model;

import com.alibaba.fastjson.JSONValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipParamMapping;
import pro.shushi.pamirs.eip.api.model.EipParamMappingItem;
import pro.shushi.pamirs.eip.api.service.model.EipOpenInterfaceService;
import pro.shushi.pamirs.eip.api.service.model.EipParamMappingService;
import pro.shushi.pamirs.framework.connectors.cdn.constant.FileConstants;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2024/9/6 11:48.
 */
@Slf4j
@Component
@Fun(EipOpenInterfaceService.FUN_NAMESPACE)
public class EipParamMappingServiceImpl implements EipParamMappingService {

    @Override
    @Function
    public EipParamMapping createOrUpdate(EipParamMapping data) {
        if (data == null || StringUtils.isBlank(data.getInterfaceName())
                || StringUtils.isBlank(data.getModel())
                || StringUtils.isBlank(data.getActionName())) {
            throw PamirsException.construct(EipExpEnumerate.EIP_PARAM_MAPPING_CREATE_OR_UPDATE_ERROR).errThrow();
        }
        if (StringUtils.isBlank(data.getViewName())) {
            // 复制页面时视图名称为空，不创建映射
            return data;
        }
        data.createOrUpdate();
        return data;
    }

    @Override
    @Function
    public EipParamMapping createOrUpdateWithExp(EipParamMapping data) {
        handlerParamMappingExp(data.getHeaderMapping());
        handlerParamMappingExp(data.getBodyMapping());
        handlerParamMappingExp(data.getQueryMapping());
        handlerParamMappingExp(data.getPathMapping());
        handlerParamMappingExp(data.getResponseMapping());
        return createOrUpdate(data);
    }

    private void handlerParamMappingExp(List<EipParamMappingItem> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }

        // 递归打平children
        List<EipParamMappingItem> flattenedList = new ArrayList<>();
        for (EipParamMappingItem mappingItem : itemList) {
            flattenChildren(mappingItem, flattenedList);
        }

        // 处理映射字段值，如果为空则去除
        List<EipParamMappingItem> filteredList = flattenedList.stream()
                .filter(item -> !StringUtils.isBlank(item.getTo()))
                .peek(item -> item.setTo(filterFrontEndExp(item.getTo())))
                .collect(Collectors.toList());

        itemList.clear();
        itemList.addAll(filteredList);
    }

    private void flattenChildren(EipParamMappingItem item, List<EipParamMappingItem> result) {
        if (item != null) {
            result.add(item);
            for (EipParamMappingItem child : Optional.ofNullable(item.getChildren()).orElse(Collections.emptyList())) {
                flattenChildren(child, result);
            }
        }
    }

    private String filterFrontEndExp(String exp) {
        try (JSONValidator validator = JSONValidator.from(exp)) {
            if (JSONValidator.Type.Array.equals(validator.getType())) {
                List<String> strings = JsonUtils.parseObjectList(exp, String.class);
                exp = String.join(FileConstants.POINT, strings);
            }
        } catch (Exception e) {
            log.error("JSON数据解析失败", e);
            throw PamirsException.construct(EipExpEnumerate.EIP_JSON_PARSING_ERROR).errThrow();
        }
        return exp.replace("activeRecord.", "");
    }
}
