package pro.shushi.pamirs.framework.orm.client.converter.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.DateUtils;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 前端时间转换服务
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
@Slf4j
public class ClientDateConverter {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            Object dateValue = null;
            if (value instanceof List) {
                List list          = (List) value;
                List dateValueList = new ArrayList();
                for (Object entry : list) {
                    Object entryValue = TypeUtils.valueOfPrimary(fieldConfig.getLtype(), (String) entry,
                            FieldFix.fixFormat(fieldConfig.getModelField()));
                    dateValueList.add(entryValue);
                }
                if (CollectionUtils.isNotEmpty(dateValueList)) {
                    dateValue = dateValueList;
                }
            } else {
                dateValue = TypeUtils.valueOfPrimary(fieldConfig.getLtype(), (String) value,
                        FieldFix.fixFormat(fieldConfig.getModelField()));
            }

            if (null != dateValue) {
                origin.put(fieldConfig.getLname(), dateValue);
            }
        }catch (Throwable e){
            String errorMsg  = String.format("ClientDateConverter in method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]",fieldConfig.getModel(),fieldConfig.getField(),fieldConfig.getTtype(),fieldConfig.getLtype(),value,value.getClass());
            log.error("ClientDateConverter in method error",errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR,e).appendMsg(errorMsg).errThrow();
        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        String lname = fieldConfig.getLname();
        Object value = origin.get(lname);
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            Object dateString = null;
            if (value instanceof List) {
                List list          = (List) value;
                List dateValueList = new ArrayList();
                for (Object entry : list) {
                    String dateValue = DateUtils.convertDate(entry, FieldFix.fixFormat(fieldConfig.getModelField()));
                    dateValueList.add(dateValue);
                }
                if (CollectionUtils.isNotEmpty(dateValueList)) {
                    dateString = dateValueList;
                }
            } else {
                dateString = DateUtils.convertDate(value, FieldFix.fixFormat(fieldConfig.getModelField()));
            }
            if (null != dateString) {
                origin.put(lname, dateString);
            }
        }catch (Throwable e){
            String errorMsg  = String.format("ClientDateConverter out method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]",fieldConfig.getModel(),fieldConfig.getField(),fieldConfig.getTtype(),fieldConfig.getLtype(),value,value.getClass());
            log.error("ClientDateConverter out method error",errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR,e).appendMsg(errorMsg).errThrow();
        }
    }

}
