package pro.shushi.pamirs.framework.gateways.graph.java.build;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GQL验证上下文
 *
 * @author Adamancy Zhang at 16:07 on 2024-11-04
 */
public class GraphQLVerifyContext {

    private final Map<String, Pair<String, String>> repeatNameMap = new HashMap<>();

    private final List<String> errors = new ArrayList<>();

    public Map<String, Pair<String, String>> getRepeatNameMap() {
        return repeatNameMap;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void verifyDataDictionary(DataDictionary dataDictionary, String name) {
        Pair<String, String> oldValue = repeatNameMap.get(name);
        String newDictionary = dataDictionary.getDictionary();
        if (oldValue == null) {
            repeatNameMap.put(name, ImmutablePair.of(newDictionary, dataDictionary.getLname()));
        } else {
            errors.add(String.format("数据字典重复定义，请检查类名是否相同。 dictionary1: %s; class1: %s; dictionary2: %s; class2: %s", oldValue.getLeft(), oldValue.getRight(), newDictionary, dataDictionary.getLname()));
        }
    }
}
