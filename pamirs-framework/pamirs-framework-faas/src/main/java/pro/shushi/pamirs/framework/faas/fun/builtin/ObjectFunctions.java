package pro.shushi.pamirs.framework.faas.fun.builtin;

import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.Optional;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.OBJECT;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 对象函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(NamespaceConstants.expression)
public class ObjectFunctions {

    @Function.Advanced(
            displayName = "判断是否为空", language = JAVA,
            builtin = true, category = OBJECT
    )
    @Function.fun("IS_NULL")
    @Function(name = "IS_NULL", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: IS_NULL(文本或控件)\n函数说明: 判断文本或控件是否为空，为空则返回true，不为空则返回false，可用于判断具体值或者控件"
    )
    public static Boolean isNull(Object a) {
        return null == a;
    }

    @Function.Advanced(
            displayName = "判断是否相等", language = JAVA,
            builtin = true, category = OBJECT
    )
    @Function.fun("EQUALS")
    @Function(name = "EQUALS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: EQUALS(A,B)\n函数说明: 判断A和B是否相等"
    )
    public static Boolean equals(Object a, Object b) {
        return ObjectUtils.equals(a, b);
    }

    @Function.Advanced(
            displayName = "获取对象属性值", language = JAVA,
            builtin = true, category = OBJECT
    )
    @Function.fun("GET")
    @Function(name = "GET", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: GET(obj,dotExpression)\n函数说明: 从对象中根据属性点表达式获取属性值"
    )
    public static Object propGet(Object obj, String dotExpression) {
        if (null == obj) {
            return null;
        }
        if (dotExpression == null) {
            return null;
        }
        String[] fields = dotExpression.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        //如果表达式是 "." 分割结果是长度为0的数组, 以下逻辑会返回当前对象
        for (String field : fields) {
            obj = FieldUtils.getFieldValue(obj, field);
            if (null == obj) {
                return null;
            }
        }
        return obj;
    }

    @Function.Advanced(
            displayName = "根据字段编码获取对象属性值", language = JAVA,
            builtin = true, category = OBJECT
    )
    @Function.fun("FIELD_GET")
    @Function(name = "FIELD_GET", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: FIELD_GET(obj,model,dotExpression)\n函数说明: 从对象中根据字段编码点表达式获取属性值"
    )
    public static Object fieldGet(Object obj, String model, String dotExpression) {
        if (null == obj) {
            return null;
        }
        if (null == model) {
            return null;
        }
        if (null == dotExpression) {
            return null;
        }
        String currentModel = model;
        String[] fields = dotExpression.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        //如果表达式是 "." 分割结果是长度为0的数组, 以下逻辑会返回当前对象
        for (String field : fields) {
            final String fieldModel = currentModel;
            ModelFieldConfig currentModelField = Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelField(fieldModel, field)).orElse(null);
            if (null == currentModelField) {
                return null;
            }
            obj = FieldUtils.getFieldValue(obj, currentModelField.getLname());
            if (null == obj) {
                return null;
            }
            currentModel = currentModelField.getReferences();
        }
        return obj;
    }

    public static ModelFieldConfig getModelFieldConfig(String model, String field) {
        ModelFieldConfig modelField = PamirsSession.getContext().getModelField(model, field);
        if (null == modelField) {
            throw PamirsException.construct(FaasExpEnumerate.BASE_EXPRESSION_FIELD_IS_NOT_EXIST_ERROR)
                    .appendMsg("model：" + model + ",field：" + field).errThrow();
        }
        return modelField;
    }

}
