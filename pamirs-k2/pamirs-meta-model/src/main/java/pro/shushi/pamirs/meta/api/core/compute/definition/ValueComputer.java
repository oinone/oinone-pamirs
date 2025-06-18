package pro.shushi.pamirs.meta.api.core.compute.definition;

import com.sun.istack.internal.NotNull;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 字段值计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/4/2 3:04 上午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ValueComputer {

    <T> void compute(ModelFieldConfig modelField, T data);

    HoldKeeper<ValueComputer> holder = new HoldKeeper<>();

    HoldKeeper<ValueComputer> expressionHolder = new HoldKeeper<>();

    static ValueComputer get() {
        return holder.supply(() -> Spider.getDefaultExtension(ValueComputer.class));
    }

    static ValueComputer getExpressionComputer() {
        return expressionHolder.supply(() -> Spider.getExtension(ValueComputer.class, NamespaceConstants.expression));
    }

    static boolean isUsingExpressionComputer(@NotNull String defaultValue) {
        return defaultValue.startsWith("${") && defaultValue.endsWith("}");
    }
}
