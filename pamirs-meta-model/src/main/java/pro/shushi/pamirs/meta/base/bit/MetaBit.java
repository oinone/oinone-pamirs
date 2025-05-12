package pro.shushi.pamirs.meta.base.bit;

import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelDirectiveBatchApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.common.spi.Spider;

/**
 * 元位系统管理
 * <p>
 * 2020/7/20 2:01 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unchecked")
public interface MetaBit {

    @SuppressWarnings("UnusedReturnValue")
    default <T> T clearMetaBit() {
        return (T) Models.modelDirective().clear(this);
    }

    @SuppressWarnings("UnusedReturnValue")
    default <T> T initMetaBit(SystemDirectiveEnum... directiveEnums) {
        return (T) Spider.getDefaultExtension(ModelDirectiveBatchApi.class).init(this, directiveEnums);
    }

    @SuppressWarnings("UnusedReturnValue")
    default <T> T initMetaBit(Long directive) {
        return (T) Spider.getDefaultExtension(ModelDirectiveBatchApi.class).init(this, directive);
    }

    default Long bitValue() {
        return Models.modelDirective().value(this);
    }

}
