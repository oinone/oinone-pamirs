package pro.shushi.pamirs.framework.connectors.data.elastic.rest.mapping;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.PropertyBuilders;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ElasticMappingManager
 *
 * @author yakir on 2022/09/01 21:29.
 * {@see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html#strict-date-time"/>}
 * {@see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/range.html"/>}
 *
 * <pre>
 *   TEXT("text", "长文本", "text"),
 *   KEYWORD("keyword", "短文本", "keyword"),
 *
 *   LONG("long", "长整型", "long -2^63 ~ 2^63-1"),
 *   INTEGER("integer", "整型", "integer -2^31 ~ 2^31-1"),
 *   SHORT("short", "短整型", "short -32768 ~ 32767"),
 *   BYTE("byte", "比特", "byte -128 ~ 127"),
 *   DOUBLE("double", "双精度浮点型", "double"),
 *   FLOAT("float", "单精度浮点型", "float"),
 *   HALF_FLOAT("half_float", "半精度浮点型", "half_float"),
 *   SCALED_FLOAT("scaled_float", "缩放浮点", "scaled_float"),
 *
 *   DATE("date", "日期", "date", "strict_date_optional_time||epoch_millis"),
 *   //DATE_NANOS("date_nanos", "日期", "date_nanos", "strict_date_optional_time||epoch_millis"),
 *
 *   BOOL("boolean", "布尔", "boolean true|false"),
 *
 *   // RANGE ...
 *
 *   OBJECT("object", "对象", "object"),
 *   NESTED("nested", "数组", "nested"),
 * </pre>
 */
@Slf4j
@Component
public class ElasticMappingManager {

    public Map<String, Property> mapping(String modelModel, List<Map<String, String>> analyzers) {

        if (null == modelModel || modelModel.isEmpty()) {
            throw new RuntimeException("获取模型编码异常");
        }

        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(modelModel);
        if (null == modelConfig || null == modelConfig.getModelDefinition()) {
            throw new RuntimeException("获取模型异常");
        }

        ModelDefinition modelDef = modelConfig.getModelDefinition();
        List<ModelField> fieldList = modelDef.getModelFields();

        Map<String, Map<String, String>> anazMap = Optional.ofNullable(analyzers)
                .map(List::stream)
                .orElse(Stream.empty())
                .collect(Collectors.toMap(_entry -> _entry.get("field"), Function.identity(), (_a, _b) -> _a));

        Map<String, Property> propertyMap = new HashMap<>();
        for (ModelField field : fieldList) {
//            if (!field.getStore()) continue;
            String tType = field.getTtype().name();
            String fieldName = field.getName();
            switch (tType) {
                case "BINARY":
                    propertyMap.put(fieldName, PropertyBuilders.binary(_builder -> _builder));
                    break;
                case "INTEGER":
                    if (Long.class.getCanonicalName().equals(field.getLtype())) {
                        propertyMap.put(fieldName, PropertyBuilders.long_(_builder -> _builder));
                    } else {
                        propertyMap.put(fieldName, PropertyBuilders.integer(_builder -> _builder));
                    }
                    break;
                case "FLOAT":
                    propertyMap.put(fieldName, PropertyBuilders.float_(_builder -> _builder));
                    break;
                case "BOOLEAN":
                    propertyMap.put(fieldName, PropertyBuilders.boolean_(_builder -> _builder));
                    break;
                case "STRING":
                    Map<String, String> anaz = anazMap.get(fieldName);
                    if (MapUtils.isNotEmpty(anaz)) {
                        final String analyzer = anaz.get("analyzer");
                        final String searchAnalyzer = anaz.get("searchAnalyzer");
                        propertyMap.put(fieldName, PropertyBuilders.text(_builder -> {
                            if (StringUtils.isNotBlank(analyzer) && !StringUtils.equalsIgnoreCase(analyzer, "standard")) {
                                _builder.analyzer(analyzer);
                            }
                            if (StringUtils.isNotBlank(searchAnalyzer) && !StringUtils.equalsIgnoreCase(searchAnalyzer, "standard")) {
                                _builder.searchAnalyzer(searchAnalyzer);
                            }
                            return _builder;
                        }));
                    } else {
                        propertyMap.put(fieldName, PropertyBuilders.keyword(_builder -> _builder));
                    }
                    break;
                case "TEXT":
                case "HTML":
                    propertyMap.put(fieldName, PropertyBuilders.text(_builder -> _builder));
                    break;
                case "ENUM":
                    propertyMap.put(fieldName, PropertyBuilders.keyword(_builder -> _builder));
                    break;
                case "DATETIME":
                case "YEAR":
                case "DATE":
                case "TIME":
                    propertyMap.put(fieldName, PropertyBuilders.date(_builder -> _builder));
                    break;
                case "UID":
                    propertyMap.put(fieldName, PropertyBuilders.long_(_builder -> _builder));
                    break;
                case "PHONE":
                case "EMAIL":
                    propertyMap.put(fieldName, PropertyBuilders.text(_builder -> _builder));
                    break;
                case "MONEY":
                    propertyMap.put(fieldName, PropertyBuilders.float_(_builder -> _builder));
                    break;
                case "MAP":
                    propertyMap.put(fieldName, PropertyBuilders.nested(_builder -> _builder));
                case "OBJ":
                case "O2O":
                case "M2O":
                    propertyMap.put(fieldName, PropertyBuilders.object(_builder -> _builder));
                    break;
                case "VOID":
                case "RELATED":
                case "O2M":
                case "M2M":
                default:
                    continue;
            }
        }
        return propertyMap;
    }
}
