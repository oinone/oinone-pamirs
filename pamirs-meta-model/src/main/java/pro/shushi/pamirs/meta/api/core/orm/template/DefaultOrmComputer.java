package pro.shushi.pamirs.meta.api.core.orm.template;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.core.compute.template.OrmComputer;
import pro.shushi.pamirs.meta.base.D;

import java.util.List;
import java.util.Map;

/**
 * orm计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class DefaultOrmComputer<T, R> implements OrmComputer<T, R> {

    @SuppressWarnings("unchecked")
    @Override
    public R compute(String model, T o,
                     OrmConverter<R> mdConverter,
                     OrmConverter<R> listConverter,
                     OrmConverter<R> arrayConverter
    ) {
        if (null == o) {
            return null;
        } else if (StringUtils.isBlank(model)) {
            return (R) o;
        } else if (Map.class.isAssignableFrom(o.getClass()) || D.class.isAssignableFrom(o.getClass())) {
            return mdConverter.compute(model, (R) o);
        } else if (List.class.isAssignableFrom(o.getClass())) {
            return listConverter.compute(model, (R) o);
        } else if (o.getClass().isArray()) {
            return arrayConverter.compute(model, (R) o);
        }
        return (R) o;
    }

}
