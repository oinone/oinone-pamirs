package pro.shushi.pamirs.framework.faas.fun.builtin;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.util.JsonUtils;

import static pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum.TEXT;
import static pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum.JAVA;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionSceneEnum.EXPRESSION;

/**
 * 文本函数
 * <p>
 * 2020/6/4 2:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(NamespaceConstants.expression)
public class TextFunctions {

    @Function.Advanced(
            displayName = "是否为空字符串", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("IS_BLANK")
    @Function(name = "IS_BLANK", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: IS_BLANK(text)\n函数说明: 判断文本字符串text是否为空"
    )
    public static Boolean isBlank(String text) {
        return StringUtils.isBlank(text);
    }

    @Function.Advanced(
            displayName = "是否以指定字符串开始", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("STARTS_WITH")
    @Function(name = "STARTS_WITH", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: STARTS_WITH(text,start)\n函数说明: 判断文本字符串text是否以文本字符串start开始，文本为空时，按照空字符串处理"
    )
    public static Boolean startsWith(String text, String start) {
        if (null == text) {
            text = "";
        }
        if (null == start) {
            return Boolean.FALSE;
        }
        return text.startsWith(start);
    }

    @Function.Advanced(
            displayName = "是否以指定字符串结束", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("ENDS_WITH")
    @Function(name = "ENDS_WITH", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: ENDS_WITH(text,start)\n函数说明: 判断文本字符串text是否以文本字符串end结束，文本为空时，按照空字符串处理"
    )
    public static Boolean endsWith(String text, String end) {
        if (null == text) {
            text = "";
        }
        if (null == end) {
            return Boolean.FALSE;
        }
        return text.endsWith(end);
    }

    @Function.Advanced(
            displayName = "包含", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("CONTAINS")
    @Function(name = "CONTAINS", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: CONTAINS(text,subtext)\n函数说明: 判断文本字符串text是否包含文本字符串subtext，文本text为空时，按照空字符串处理"
    )
    public static Boolean contains(String text, String subtext) {
        if (null == text) {
            text = "";
        }
        if (null == subtext) {
            return Boolean.FALSE;
        }
        return text.contains(subtext);
    }

    @Function.Advanced(
            displayName = "过滤首尾空格", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("TRIM")
    @Function(name = "TRIM", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: TRIM(text)\n函数说明: 去掉文本字符串text中的首尾空格"
    )
    public static String trim(String text) {
        if (null == text) {
            return null;
        }
        return StringUtils.trim(text);
    }

    @Function.Advanced(
            displayName = "大写", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("UPPER")
    @Function(name = "UPPER", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: UPPER(text)\n函数说明: 大写文本字符串text"
    )
    public static String upper(String text) {
        if (null == text) {
            return null;
        }
        return text.toUpperCase();
    }

    @Function.Advanced(
            displayName = "小写", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("LOWER")
    @Function(name = "LOWER", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LOWER(text)\n函数说明: 小写文本字符串text"
    )
    public static String lower(String text) {
        if (null == text) {
            return null;
        }
        return text.toLowerCase();
    }

    @Function.Advanced(
            displayName = "获取字符串长度", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("LEN")
    @Function(name = "LEN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: LEN(text)\n函数说明: 获取文本字符串text的长度，文本为空时，按照空字符串处理"
    )
    public static Integer len(String content) {
        if (null == content) {
            content = "";
        }
        return content.length();
    }

    @Function.Advanced(
            displayName = "替换字符串", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("REPLACE")
    @Function(name = "REPLACE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: REPLACE(text,oldtext,newtext)\n函数说明: 使用文本字符串newtext替换文本字符串text中的文本字符串oldtext"
    )
    public static String replace(String text, String oldtext, String newtext) {
        if (null == text) {
            return null;
        }
        if (oldtext == null || newtext == null) {
            return text;
        }
        return text.replace(oldtext, newtext);
    }

    @Function.Advanced(
            displayName = "连接字符串", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("JOIN")
    @Function(name = "JOIN", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: JOIN(text,join)\n函数说明: 将文本字符串text连接文本字符串join，文本为空时，按照空字符串处理"
    )
    public static Object join(Object text, Object join) {
        if (null == text) {
            text = "";
        }
        if (null == join) {
            join = "";
        }
        return text.toString() + join.toString();
    }

    @Function.Advanced(
            displayName = "反序列化JSON字符串", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("PARSE")
    @Function(name = "PARSE", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: PARSE(text)\n函数说明: 将JSON文本字符串text反序列化为集合或者map"
    )
    public static Object parse(String text) {
        if (null == text) {
            return null;
        }
        try {
            return JsonUtils.parseObject(text);
        } catch (Exception e) {
            return JsonUtils.parseObjectList(text);
        }
    }

    @Function.Advanced(
            displayName = "将记录序列化为JSON字符串", language = JAVA,
            builtin = true, category = TEXT
    )
    @Function.fun("JSON")
    @Function(name = "JSON", scene = {EXPRESSION}, openLevel = LOCAL,
            summary = "函数示例: JSON(object)\n函数说明: 将记录object序列化为JSON字符串"
    )
    public static String json(Object obj) {
        if (null == obj) {
            return null;
        }
        return JsonUtils.toJSONString(obj);
    }

}
