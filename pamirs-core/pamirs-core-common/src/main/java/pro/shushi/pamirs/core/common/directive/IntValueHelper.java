package pro.shushi.pamirs.core.common.directive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <h>int value helper</h>
 * <p>
 * design principle:
 * 1、Stack depth le 1
 * 2、All compute method must be bit operation
 * </p>
 *
 * @author Adamancy Zhang
 * @date 2020-10-22 13:05
 */
public class IntValueHelper {

    private IntValueHelper() {
        //reject create object
    }

    public static <T extends IntValue> T intValueOf(T[] targets, int value) {
        for (T target : targets) {
            if (target.intValue() == value) {
                return target;
            }
        }
        return null;
    }

    public static <T extends IntValue> T intValueOf(Iterable<T> targets, int value) {
        for (T target : targets) {
            if (target.intValue() == value) {
                return target;
            }
        }
        return null;
    }

    public static <T extends IntValue> Collection<T> intValuesOf(T[] targets, int value) {
        List<T> list = new ArrayList<>();
        for (T target : targets) {
            if ((target.intValue() & value) != 0) {
                list.add(target);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T extends IntValue> Collection<T> intValuesOf(Iterable<T> targets, int value) {
        List<T> list = new ArrayList<>();
        for (T target : targets) {
            if ((target.intValue() & value) != 0) {
                list.add(target);
            }
        }
        return Collections.unmodifiableList(list);
    }
}
