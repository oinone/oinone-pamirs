package pro.shushi.pamirs.meta.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 4:23 下午
 */
public class PStringUtils extends StringUtils {

    private static final List<String> NUMBERS = Lists.newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    @SuppressWarnings("unused")
    public static String column2FieldName(String column) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (column == null || column.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!column.contains("_")) {
            // 不含下划线，仅将首字母小写
            return column.substring(0, 1).toLowerCase() + column.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = column.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，全部字母都小写
                result.append(camel.toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static String fieldName2Column(String field) {
        if (isBlank(field)) {
            return EMPTY;
        }
        int len = field.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = field.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(CharacterConstants.SEPARATOR_UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String dotName2ShortName(String name) {
        if (isBlank(name)) {
            return EMPTY;
        }
        if (name.contains(CharacterConstants.SEPARATOR_DOT)) {
            return StringUtils.substringAfterLast(name, CharacterConstants.SEPARATOR_DOT);
        }
        return name;
    }

    public static String camelCaseFromModel(String model) {
        if (null == model) {
            return null;
        }
        String name = model;
        if (model.contains(CharacterConstants.SEPARATOR_DOT)) {
            name = StringUtils.substringAfterLast(model, CharacterConstants.SEPARATOR_DOT);
        }

        return StringUtils.uncapitalize(name);
    }

    public static List<String> trim(String[] array) {
        if (null == array) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String item : array) {
            list.add(item.trim());
        }
        return list;
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     * @return ignore
     */
    public static boolean checkValNotNull(Object object) {
        if (object instanceof CharSequence) {
            return isNotBlank((CharSequence) object);
        }
        return object != null;
    }

    /**
     * 判断对象是否为空
     *
     * @param object ignore
     * @return ignore
     */
    @SuppressWarnings("unused")
    public static boolean checkValNull(Object object) {
        return !checkValNotNull(object);
    }

    public static String valueOfObj(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    public static boolean startWithNumber(String str) {
        if (null == str || str.isEmpty()) {
            return true;
        }
        String first = str.trim().charAt(0) + "";

        return NUMBERS.contains(first);
    }

    public static String toStringAndNullToEmpty(Object obj) {
        if (null == obj) {
            return "";
        }
        return obj.toString();
    }

    /**
     * 将字符串text中由openToken和closeToken组成的占位符依次替换为args数组中的值
     * @param openToken
     * @param closeToken
     * @param text
     * @param args
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, Object... args) {
        if (args == null || args.length <= 0) {
            return text;
        }
        int argsIndex = 0;
        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        // search open token
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {

                    String value = (argsIndex <= args.length - 1) ?
                            (args[argsIndex] == null ? "" : args[argsIndex].toString()) : expression.toString();
                    builder.append(value);
                    offset = end + closeToken.length();
                    argsIndex++;

                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }
    public static String parse0(String text, Object... args) {
        return parse("${", "}", text, args);
    }
    public static String parse1(String text, Object... args) {
        return parse("{", "}", text, args);
    }

    public static void main(String[] args) {
        String string = "生成默认导出模板成功 [Model {}] [ViewName {}]";
        System.out.printf(parse1(string,"user.PamirsUser","test.vue"));

    }

}
