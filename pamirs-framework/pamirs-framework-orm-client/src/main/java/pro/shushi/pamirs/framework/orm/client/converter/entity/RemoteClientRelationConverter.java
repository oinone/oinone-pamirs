package pro.shushi.pamirs.framework.orm.client.converter.entity;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.orm.client.converter.RemoteClientDataConverter;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * 前端转换服务
 * <p>
 * 递归遍历
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 */
@Component
public class RemoteClientRelationConverter {

    @SuppressWarnings("unused")
    @Resource
    private RemoteClientDataConverter remoteClientDataConverter;

    public void in(ModelComputeContext totalContext, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        if (TtypeEnum.isRelationType(ttype)) {
            String model = Optional.ofNullable(Models.api().getModel(value)).orElse(fieldConfig.getReferences());
            origin.put(fieldConfig.getLname(), remoteClientDataConverter.in(totalContext, model, value));
        }
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        String lname = fieldConfig.getLname();
        Object value = origin.get(lname);
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        if (TtypeEnum.isRelationType(ttype)) {
            String model = Optional.ofNullable(Models.api().getModel(value)).orElse(fieldConfig.getReferences());
            origin.put(lname, remoteClientDataConverter.out(model, value));
        }
    }

}
