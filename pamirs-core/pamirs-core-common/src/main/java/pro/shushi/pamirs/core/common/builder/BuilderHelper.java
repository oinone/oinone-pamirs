package pro.shushi.pamirs.core.common.builder;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder Utils
 *
 * @author Adamancy Zhang at 12:06 on 2024-01-04
 */
public class BuilderHelper {

    public static <T> T build(IBuilder<T> builder) {
        if (builder == null) {
            return null;
        }
        return builder.build();
    }

    public static <T> List<T> batchBuild(List<IBuilder<T>> builders) {
        if (CollectionUtils.isEmpty(builders)) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (IBuilder<T> builder : builders) {
            T target = builder.build();
            if (target != null) {
                list.add(target);
            }
        }
        return list;
    }
}
