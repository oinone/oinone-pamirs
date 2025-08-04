package pro.shushi.pamirs.framework.orm.converter.entity.relation;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.systems.relation.RelationManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * m2o数据转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ManyToOneRelationConverter {

    @Resource
    private RelationManager relationManager;

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> dMap) {
        // 关系一端的关系字段处理
        relationManager.fillRelationFieldValueFromRelation(fieldConfig, dMap);
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> dMap) {
        // 关系一端的关系字段处理
        relationManager.fillManyToOneValueFromRelation(fieldConfig, dMap);
    }

}
