package pro.shushi.pamirs.boot.standard.checker.helper;

import com.alibaba.fastjson.JSONArray;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.Iterator;

/**
 * 字符串数组检查
 *
 * @author Adamancy Zhang at 10:05 on 2024-10-16
 */
public class StringArrayChecker implements EnvironmentKey.Checker {

    public static final StringArrayChecker IMMUTABLE = new StringArrayChecker(Feature.IMMUTABLE);

    public static final StringArrayChecker ONLY_ADD = new StringArrayChecker(Feature.ONLY_ADD);

    private final Feature feature;

    private StringArrayChecker(Feature feature) {
        this.feature = feature;
    }

    @Override
    public PlatformEnvironment check(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, PlatformEnvironment historyEnvironment) {
        String oldArrayValue = currentEnvironment.getValue();
        String newArrayValue = currentEnvironment.getValue();
        if (oldArrayValue != null) {
            JSONArray oldArray = JSONArray.parseArray(oldArrayValue);
            JSONArray newArray;
            if (newArrayValue == null) {
                newArray = new JSONArray();
            } else {
                newArray = JSONArray.parseArray(newArrayValue);
            }
            switch (feature) {
                case IMMUTABLE:
                    immutable(context, currentEnvironment, oldArray, newArray);
                    break;
                case ONLY_ADD:
                    onlyAdd(context, currentEnvironment, oldArray, newArray);
                    break;
            }
        }
        return currentEnvironment;
    }

    private void immutable(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, JSONArray oldArray, JSONArray newArray) {
        onlyAdd(context, currentEnvironment, oldArray, newArray);
        for (Object newItem : newArray) {
            String newValue = String.valueOf(newItem);
            context.addError(currentEnvironment, "不允许追加配置: " + newValue);
        }
    }

    private void onlyAdd(EnvironmentCheckContext context, PlatformEnvironment currentEnvironment, JSONArray oldArray, JSONArray newArray) {
        for (Object oldItem : oldArray) {
            String oldValue = String.valueOf(oldItem);
            String newValue = remove(newArray, oldValue);
            if (newValue == null) {
                context.addError(currentEnvironment, "缺少配置: " + oldValue);
            }
        }
    }

    private String remove(JSONArray array, String target) {
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            String value = String.valueOf(iterator.next());
            if (target.equals(value)) {
                iterator.remove();
                return value;
            }
        }
        return null;
    }

    public enum Feature {

        /**
         * 不可变数组
         */
        IMMUTABLE,

        /**
         * 仅允许追加
         */
        ONLY_ADD
    }
}