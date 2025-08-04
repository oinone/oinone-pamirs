package pro.shushi.pamirs.framework.orm.client.converter.entity;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.orm.client.converter.DefaultClientDataConverter;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * 前端转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
@Slf4j
public class ClientRelationConverter {

    @SuppressWarnings("unused")
    @Resource
    private DefaultClientDataConverter frontEndDataConverter;

    public void in(ModelComputeContext totalContext, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            String ttype = fieldConfig.getTtype();
            if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
                ttype = fieldConfig.getRelatedTtype();
            }
            if (TtypeEnum.isRelationType(ttype)) {
                String model = Optional.ofNullable(Models.api().getModel(value)).orElse(fieldConfig.getReferences());
                origin.put(fieldConfig.getLname(), frontEndDataConverter.in(totalContext, model, value));
            }
        } catch (Exception e) {
            String errorMsg = String.format("ClientRelationConverter in method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]", fieldConfig.getModel(), fieldConfig.getField(), fieldConfig.getTtype(), fieldConfig.getLtype(), value, value.getClass());
            log.error("ClientRelationConverter in method error", errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR, e).appendMsg(errorMsg).errThrow();
        }
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        String lname = fieldConfig.getLname();
        Object value = origin.get(lname);
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            String ttype = fieldConfig.getTtype();
            if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
                ttype = fieldConfig.getRelatedTtype();
            }
            if (TtypeEnum.isRelationType(ttype)) {
                String model = Optional.ofNullable(Models.api().getModel(value)).orElse(fieldConfig.getReferences());
                origin.put(lname, frontEndDataConverter.out(model, value));
            }
        } catch (Exception e) {
            String errorMsg = String.format("ClientRelationConverter out method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]", fieldConfig.getModel(), fieldConfig.getField(), fieldConfig.getTtype(), fieldConfig.getLtype(), value, value.getClass());
            log.error("ClientRelationConverter out method error", errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR, e).appendMsg(errorMsg).errThrow();
        }
    }

}
