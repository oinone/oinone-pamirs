package pro.shushi.pamirs.core.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 属性帮助类
 *
 * @author Adamancy Zhang at 13:54 on 2021-08-04
 */
public class PropertyHelper {

    private PropertyHelper() {
        //reject create object
    }

    public static <T, R> PropertyIgnoreExecutor<T, R> ignoreProperty(T data) {
        return new PropertyIgnoreExecutor<>(data);
    }

    public static class PropertyIgnoreExecutor<T, R> {

        private final T data;

        private final List<GetterAndSetter<T, Object>> methods = new ArrayList<>();

        public PropertyIgnoreExecutor(T data) {
            this.data = data;
        }

        @SuppressWarnings("unchecked")
        public <V> PropertyIgnoreExecutor<T, R> oops(Function<T, V> getter, BiConsumer<T, V> setter, Consumer<T> unsetter) {
            this.methods.add(new GetterAndSetter<>((Function<T, Object>) getter, (BiConsumer<T, Object>) setter, unsetter));
            return this;
        }

        public R execute(Function<T, R> function) {
            List<Object> values = new ArrayList<>();
            for (GetterAndSetter<T, ?> method : methods) {
                values.add(method.getter.apply(data));
                method.unsetter.accept(data);
            }
            R result = function.apply(data);
            int index = 0;
            for (GetterAndSetter<T, Object> method : methods) {
                method.setter.accept(data, values.get(index));
                index++;
            }
            return result;
        }

        public void executeWithoutResult(Consumer<T> consumer) {
            execute((data) -> {
                consumer.accept(data);
                return null;
            });
        }
    }

    private static class GetterAndSetter<T, R> {

        private final Function<T, R> getter;

        private final BiConsumer<T, R> setter;

        private final Consumer<T> unsetter;

        private GetterAndSetter(Function<T, R> getter, BiConsumer<T, R> setter, Consumer<T> unsetter) {
            this.getter = getter;
            this.setter = setter;
            this.unsetter = unsetter;
        }
    }
}
