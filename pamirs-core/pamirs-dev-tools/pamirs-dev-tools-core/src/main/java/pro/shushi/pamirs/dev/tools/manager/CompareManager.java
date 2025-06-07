package pro.shushi.pamirs.dev.tools.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.ServerAction;
import pro.shushi.pamirs.boot.base.model.UeModel;
import pro.shushi.pamirs.boot.base.model.UrlAction;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;

/**
 * @author haibo(xf.z @ shushi.pro)
 * @date 2024/2/5 11:39
 */
@Slf4j
@Component
public class CompareManager {


    public static String compareModel(UeModel modelDefinition, ModelConfig modelConfig) {
        List<String> includeList = new ArrayList<>();
        includeList.add("uniques");
        includeList.add("systemSource");
        includeList.add("table");
        includeList.add("pk");
        includeList.add("pkList");
        includeList.add("createDate");
        includeList.add("moduleAbbr");
        includeList.add("lname");
        includeList.add("dsModule");
        includeList.add("displayName");
        includeList.add("type");
        includeList.add("model");
        includeList.add("name");
        includeList.add("module");
        Map<String, Object> db = modelDefinition != null ? modelDefinition.get_d() : new HashMap<>();
        Map<String, Object> cache = modelConfig != null ? modelConfig.getModelDefinition().get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }

    public static String compareDictionary(DataDictionary dbDict, DataDictionary cacheDict) {
        List<String> includeList = new ArrayList<>();
        includeList.add("module");
        includeList.add("displayName");
        includeList.add("dictionary");
        includeList.add("name");
        includeList.add("lname");
        includeList.add("summary");
        includeList.add("valueType");
        includeList.add("lname");
        includeList.add("type");
        includeList.add("state");
        includeList.add("show");
        includeList.add("priority");
        includeList.add("name");
        includeList.add("options");
        Map<String, Object> db = dbDict != null ? dbDict.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheDict != null ? cacheDict.get_d() : new HashMap<>();
        return compareMap(db, cache, includeList);
    }

    public static String compareSequence(SequenceConfig dbSeq, SequenceConfig cacheSeq) {
        List<String> includeList = new ArrayList<>();
        includeList.add("module");
        includeList.add("displayName");
        includeList.add("summary");
        includeList.add("code");
        includeList.add("prefix");
        includeList.add("suffix");
        includeList.add("size");
        includeList.add("format");
        includeList.add("step");
        includeList.add("isRandomStep");
        includeList.add("initial");
        includeList.add("sequence");
        includeList.add("zeroingPeriod");
        includeList.add("show");
        includeList.add("priority");
        Map<String, Object> db = dbSeq != null ? dbSeq.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheSeq != null ? cacheSeq.get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }

    public static String compareFun(FunctionDefinition dbFun, FunctionDefinition cacheFun) {
        List<String> includeList = new ArrayList<>();
        includeList.add("displayName");
        includeList.add("module");
        includeList.add("namespace");
        includeList.add("fun");
        includeList.add("name");
        includeList.add("language");
        includeList.add("type");
        includeList.add("dataManager");
        includeList.add("beanName");
        includeList.add("source");
        includeList.add("openLevel");
        includeList.add("description");
        includeList.add("category");
        includeList.add("isBuiltin");
        includeList.add("transactionConfig");
        includeList.add("group");
        includeList.add("version");
        includeList.add("timeout");
        Map<String, Object> db = dbFun != null ? dbFun.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheFun != null ? cacheFun.get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }

    public static String compareExtPoint(ExtPointImplementation dbExt, ExtPointImplementation cacheFun) {
        List<String> includeList = new ArrayList<>();
        includeList.add("displayName");
        includeList.add("name");
        includeList.add("namespace");
        includeList.add("executeNamespace");
        includeList.add("executeFun");
        includeList.add("description");
        includeList.add("priority");
        includeList.add("expression");
        includeList.add("active");
        Map<String, Object> db = dbExt != null ? dbExt.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheFun != null ? cacheFun.get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }

    public static String compareField(ModelField modelField, ModelFieldConfig modelFieldConfig) {
        List<String> includeList = new ArrayList<>();
        includeList.add("field");
        includeList.add("displayName");
        includeList.add("ltypeT");
        includeList.add("ttype");
        includeList.add("ltype");
        includeList.add("lname");
        includeList.add("store");
        includeList.add("column");
        includeList.add("relationStore");
        includeList.add("relatedList");
        includeList.add("through");
        includeList.add("summary");
        includeList.add("invisible");
        includeList.add("options");
        includeList.add("relationFields");
        includeList.add("multi");
        includeList.add("name");
        includeList.add("related");
        includeList.add("ordering");
        includeList.add("translate");
        includeList.add("pk");
        includeList.add("dictionary");
        includeList.add("throughRelationFieldList");
        includeList.add("pkIndex");
        includeList.add("size");
        includeList.add("required");
        includeList.add("throughRelationFields");
        includeList.add("referenceFieldList");
        includeList.add("unique");

        Map<String, Object> db = modelField != null ? modelField.get_d() : new HashMap<>();
        Map<String, Object> cache = modelFieldConfig != null ? modelFieldConfig.getModelField().get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }

    public static String compareServerAction(ServerAction action, ServerAction cacheAction) {
        List<String> includeList = new ArrayList<>();
        includeList.add("id");
        includeList.add("systemSource");
        includeList.add("actionType");
        includeList.add("bindingType");
        includeList.add("bindingView");
        includeList.add("summary");
        includeList.add("fun");
        includeList.add("contextType");
        includeList.add("label");
        includeList.add("bindingViewName");
        includeList.add("displayName");
        includeList.add("description");
        includeList.add("name");
        Map<String, Object> db = action != null ? action.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheAction != null ? cacheAction.getModelDefinition().get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }


    public static String compareViewAction(ViewAction action, ViewAction cacheAction) {
        List<String> includeList = new ArrayList<>();
        includeList.add("id");
        includeList.add("displayName");
        includeList.add("actionType");
        includeList.add("bindingType");
        includeList.add("bindingView");
        includeList.add("resViewName");
        includeList.add("resModel");
        includeList.add("modelName");
        includeList.add("context");
        includeList.add("rule");
        includeList.add("resModule");
        includeList.add("resView");
        includeList.add("summary");
        includeList.add("fun");
        includeList.add("contextType");
        includeList.add("label");
        includeList.add("bindingViewName");
        includeList.add("displayName");
        includeList.add("description");
        includeList.add("name");
        Map<String, Object> db = action != null ? action.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheAction != null ? cacheAction.getModelDefinition().get_d() : new HashMap<>();


        return compareMap(db, cache, includeList);
    }

    public static String compareUrlAction(UrlAction action, UrlAction cacheAction) {
        List<String> includeList = new ArrayList<>();
        includeList.add("id");
        includeList.add("systemSource");
        includeList.add("actionType");
        includeList.add("bindingType");
        includeList.add("bindingView");
        includeList.add("summary");
        includeList.add("contextType");
        includeList.add("label");
        includeList.add("bindingViewName");
        includeList.add("displayName");
        includeList.add("description");
        includeList.add("name");
        includeList.add("url");
        Map<String, Object> db = action != null ? action.get_d() : new HashMap<>();
        Map<String, Object> cache = cacheAction != null ? cacheAction.getModelDefinition().get_d() : new HashMap<>();

        return compareMap(db, cache, includeList);
    }

    private static String compareMap(Map<String, Object> db, Map<String, Object> cache, List<String> includeList) {
        Map<String, String> diffMap = new HashMap<>();
        db.forEach((k, v) -> {
            StringBuilder diff = new StringBuilder();
            Object cacheV = cache.get(k);
            if (cacheV != null && cacheV instanceof Collection) {
                cacheV = JsonUtils.toJSONString(cacheV);
            }
            if (v != null && v instanceof Collection) {
                v = JsonUtils.toJSONString(v);
            }
            if (cache.containsKey(k) &&
                    ((v != null && !v.equals(cacheV))
                            || (v == null && cacheV != null))) {
                diffMap.put(k, diff.append("db:").append(v).append(" cache:").append(cacheV).toString());
            }
        });

        cache.forEach((k, v) -> {
            StringBuilder diff = new StringBuilder();
            if (!db.containsKey(k) && cache.get(k) != null) {
                diffMap.put(k, diff.append("db:").append("null").append(" cache:").append(v).toString());
            }
        });

        Iterator<Map.Entry<String, String>> iterator = diffMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!includeList.contains(entry.getKey())) {
                iterator.remove();
            }
        }

        return JsonUtils.toJSONString(diffMap, true);
    }
}
