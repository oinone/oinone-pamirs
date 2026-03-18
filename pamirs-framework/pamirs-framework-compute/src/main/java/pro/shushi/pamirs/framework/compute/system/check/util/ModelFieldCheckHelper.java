package pro.shushi.pamirs.framework.compute.system.check.util;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.api.Exp;
import pro.shushi.pamirs.meta.api.core.faas.ExpressionApi;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Supplier;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.*;

/**
 * 字段校验帮助类
 * <p>
 * 2021/3/18 11:56 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ModelFieldCheckHelper {

    public static boolean checkRequiredAndMinMax(boolean returnWhenError, ModelField modelField,
                                                 Map<String, Object> expressionContext, Object fieldValue) {
        // 必填校验
        if (null == fieldValue) {
            boolean required = null != modelField.getRequired() && modelField.getRequired();
            String requiredCondition = modelField.getRequiredCondition();
            if (!required && StringUtils.isNotBlank(requiredCondition)) {
                // 组装上下文
                Spider.getDefaultExtension(ExpressionApi.class).construct(null,
                        modelField.getModel(), modelField.getField(), expressionContext);
                required = Exp.<Boolean>run(requiredCondition, expressionContext);
            }
            if (required) {
                PamirsSession.getMessageHub().error(BASE_CHECK_FIELD_REQUIRED_ERROR);
                return !returnWhenError;
            }
            return true;
        }
        // min和max校验
        boolean result = true;
        if (TtypeEnum.isStringType(modelField.getTtype().value())) {
            if (StringUtils.isNotBlank(modelField.getMin())) {
                if (((String) fieldValue).length() < Long.parseLong(modelField.getMin())) {
                    PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_SIZE_MIN_ERROR)
                            .append(modelField.getMin() + CharacterConstants.SEPARATOR_EMPTY + I18nUtils.getMessage("pamirs-framework-compute.ModelFieldCheckHelper.characters")));
                    result = false;
                }
            }
            if (StringUtils.isNotBlank(modelField.getMax())) {
                if (((String) fieldValue).length() > Long.parseLong(modelField.getMax())) {
                    PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_SIZE_MAX_ERROR)
                            .append(modelField.getMax() + CharacterConstants.SEPARATOR_EMPTY + I18nUtils.getMessage("pamirs-framework-compute.ModelFieldCheckHelper.characters")));
                    result = false;
                }
            }
        } else if (TtypeEnum.isDateType(modelField.getTtype().value())) {
            if (StringUtils.isNotBlank(modelField.getMin())) {
                if (DateUtils.toDate(fieldValue).compareTo(DateUtils.toDate(modelField.getMin())) < 0) {
                    PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_DATE_MIN_ERROR)
                            .append(modelField.getMin() + CharacterConstants.SEPARATOR_EMPTY));
                    result = false;
                }
            }
            if (StringUtils.isNotBlank(modelField.getMax())) {
                if (DateUtils.toDate(fieldValue).compareTo(DateUtils.toDate(modelField.getMax())) > 0) {
                    PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_DATE_MAX_ERROR)
                            .append(modelField.getMax() + CharacterConstants.SEPARATOR_EMPTY));
                    result = false;
                }
            }
        } else if (TtypeEnum.INTEGER.value().equals(modelField.getTtype().value())) {
            result = checkNumberMinMax(modelField, fieldValue,
                    () -> Long.parseLong(fieldValue + CharacterConstants.SEPARATOR_EMPTY) >= Long.parseLong(modelField.getMin()),
                    () -> Long.parseLong(fieldValue + CharacterConstants.SEPARATOR_EMPTY) <= Long.parseLong(modelField.getMax()));
        } else if (TtypeEnum.FLOAT.value().equals(modelField.getTtype().value())) {
            result = checkNumberMinMax(modelField, fieldValue,
                    () -> Double.parseDouble(fieldValue + CharacterConstants.SEPARATOR_EMPTY) >= Double.parseDouble(modelField.getMin()),
                    () -> Double.parseDouble(fieldValue + CharacterConstants.SEPARATOR_EMPTY) <= Double.parseDouble(modelField.getMax()));
        } else if (TtypeEnum.MONEY.value().equals(modelField.getTtype().value())) {
            result = checkNumberMinMax(modelField, fieldValue,
                    () -> new BigDecimal(fieldValue + CharacterConstants.SEPARATOR_EMPTY).compareTo(new BigDecimal(modelField.getMin())) >= 0,
                    () -> new BigDecimal(fieldValue + CharacterConstants.SEPARATOR_EMPTY).compareTo(new BigDecimal(modelField.getMax())) <= 0);
        }
        return result;
    }

    public static boolean checkNumberMinMax(ModelField modelField, Object fieldValue, Supplier<Boolean> checkMin, Supplier<Boolean> checkMax) {
        boolean result = true;
        if (StringUtils.isNotBlank(modelField.getMin()) && !Field.field.NEGATIVE_INFINITY.equals(modelField.getMin())) {
            if (null == fieldValue || !checkMin.get()) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_MIN_ERROR)
                        .append(modelField.getMin() + CharacterConstants.SEPARATOR_EMPTY));
                result = false;
            }
        }
        if (null != fieldValue && StringUtils.isNotBlank(modelField.getMax()) && !Field.field.POSITIVE_INFINITY.equals(modelField.getMax())) {
            if (!checkMax.get()) {
                PamirsSession.getMessageHub().msg(Message.init().error(BASE_CHECK_FIELD_MAX_ERROR)
                        .append(modelField.getMax() + CharacterConstants.SEPARATOR_EMPTY));
                result = false;
            }
        }
        return result;
    }

}
