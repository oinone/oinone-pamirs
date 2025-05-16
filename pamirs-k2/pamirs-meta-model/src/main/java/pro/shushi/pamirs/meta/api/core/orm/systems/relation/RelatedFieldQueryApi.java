package pro.shushi.pamirs.meta.api.core.orm.systems.relation;

import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.model.RelatedValue;

/**
 * 引用字段转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
public interface RelatedFieldQueryApi {

    RelatedValue queryRelated(ModelFieldConfig modelFieldConfig, Object data);

}
