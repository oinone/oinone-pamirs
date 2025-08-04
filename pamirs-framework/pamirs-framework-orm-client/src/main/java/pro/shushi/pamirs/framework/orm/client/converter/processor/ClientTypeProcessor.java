package pro.shushi.pamirs.framework.orm.client.converter.processor;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.client.converter.entity.ClientBigIntegerConverter;
import pro.shushi.pamirs.framework.orm.client.converter.entity.ClientDateConverter;
import pro.shushi.pamirs.framework.orm.client.converter.entity.ClientEnumConverter;
import pro.shushi.pamirs.framework.orm.client.converter.entity.ClientRelationConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.FieldComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.template.function.FieldConverterApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import jakarta.annotation.Resource;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.enmu.BaseEnum.caseValue;
import static pro.shushi.pamirs.meta.common.enmu.BaseEnum.cases;
import static pro.shushi.pamirs.meta.enmu.TtypeEnum.*;

/**
 * 前端类型转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientTypeProcessor implements FieldConverterApi {

    @Resource
    private ClientBigIntegerConverter clientBigIntegerConverter;

    @Resource
    private ClientEnumConverter clientEnumConverter;

    @Resource
    private ClientDateConverter clientDateConverter;

    @Resource
    private ClientRelationConverter clientRelationConverter;

    @Override
    public void in(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        ModelComputeContext totalContext = context.getTotalContext();
        // 处理引用字段类型
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        // 类型转换
        TtypeEnum.switches(ttype, caseValue(),
                cases(INTEGER).to(() -> clientBigIntegerConverter.in(fieldConfig, origin)),// 大整数处理
                cases(DATETIME, YEAR, DATE, TIME).to(() -> clientDateConverter.in(fieldConfig, origin)),// 时间类型处理
                cases(ENUM).to(() -> clientEnumConverter.in(fieldConfig, origin)),// 枚举类型处理
                cases(O2M, M2O, M2M, O2O).to(() -> clientRelationConverter.in(totalContext, fieldConfig, origin))// 关系类型处理
        );
    }

    @Override
    public void out(FieldComputeContext context, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        // 处理引用字段类型
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        // 类型转换
        TtypeEnum.switches(ttype, caseValue(),
                cases(INTEGER).to(() -> clientBigIntegerConverter.out(fieldConfig, origin)),// 大整数处理
                cases(DATETIME, YEAR, DATE, TIME).to(() -> clientDateConverter.out(fieldConfig, origin)), // 时间类型处理
                cases(ENUM).to(() -> clientEnumConverter.out(fieldConfig, origin)), // 枚举类型处理
                cases(O2M, M2O, M2M, O2O).to(() -> clientRelationConverter.out(fieldConfig, origin)) // 关系类型处理
        );
    }

}
