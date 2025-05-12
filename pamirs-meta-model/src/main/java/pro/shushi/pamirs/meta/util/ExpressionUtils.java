package pro.shushi.pamirs.meta.util;

import com.google.common.collect.Sets;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 表达式工具类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:20 下午
 */
@Slf4j
public class ExpressionUtils {

    private final static Pattern EXP_PATTERN = Pattern.compile("\\b[A-Za-z_][A-Za-z_0-9\\.]*\\b");
    private final static Pattern SUB_PATTERN = Pattern.compile("(\\b|\\B)\\.[A-Za-z_][A-Za-z_0-9\\.]*\\b");
    private final static Pattern OPERATOR_PATTERN_ONE = Pattern.compile("^[A-Za-z_\\(]");
    private final static Pattern OPERATOR_PATTERN_TWO = Pattern.compile("[A-Za-z_0-9\\)]$");
    private final static Pattern FUN_PATTERN = Pattern.compile("\\b[A-Za-z_][A-Za-z_0-9\\.]*\\([A-Za-z_][A-Za-z_0-9\\s\\,]*\\)\\B");
    private final static Pattern FUN_PATTERN_ONE = Pattern.compile("\\b[A-Za-z_][A-Za-z_0-9\\.]*\\(\\b");
    private final static Pattern FUN_PATTERN_TWO = Pattern.compile("\\b[A-Za-z_][A-Za-z_0-9\\.]*\\(\\B");
    private final static Pattern IGNORE_PATTERN = Pattern.compile("\".*?\"");
    private final static Pattern NUM_PATTERN = Pattern.compile("^[0-9]+.?[0-9]*$");
    private final static Pattern DATE_PATTERN = Pattern.compile("((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|\n" +
            "((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|\n" +
            "((0[48]|[2468][048]|[3579][26])00))-02-29))\n" +
            "\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])\\.[0-9]{0,6}$");

    public static String fetchJavaPlaceholder(Function function) {
        return Fun.class.getName() + ".run(\"" + function.getNamespace() + "\",\"" + function.getFun() + "\",";
    }

    /**
     * 获取所有变量名，包括函数名，子属性
     *
     * @param content 内容
     * @return 变量名
     */
    public static List<String> fetchExpVariable(String content) {
        return fetchVariable(content, EXP_PATTERN);
    }

    /**
     * 获取子属性
     *
     * @param content 内容
     * @return 子属性列表
     */
    public static List<String> fetchSubVariable(String content) {
        return fetchVariable(content, SUB_PATTERN);
    }

    public static boolean isValidOperatorVariable(String content) {
        return OPERATOR_PATTERN_ONE.matcher(content).matches() && OPERATOR_PATTERN_TWO.matcher(content).matches();
    }

    public static boolean isValidSingleMethod(String content) {
        return FUN_PATTERN.matcher(content).matches();
    }

    public static boolean isNum(String content) {
        return NUM_PATTERN.matcher(content).matches();
    }

    public static boolean isDate(String content) {
        return DATE_PATTERN.matcher(content).matches();
    }

    /**
     * 获取变量名，不包含函数名
     *
     * @param content 内容
     * @return 变量名列表
     */
    public static List<String> fetchStrictVariable(String content) {
        return new ArrayList<>(Sets.newHashSet(fetchOriginStrictVariable(content)));
    }

    /**
     * 获取变量名，不包含函数名，可能重复
     *
     * @param content 内容
     * @return 变量名列表
     */
    public static List<String> fetchOriginStrictVariable(String content) {
        content = ignoreString(content);
        List<String> vars = fetchExpVariable(content);
        List<String> funs = fetchFunQuote(content);
        List<String> subVars = fetchSubVariable(content);
        return vars.stream().filter(v -> !funs.contains(v + "(") && !subVars.contains("." + v)).collect(Collectors.toList());
    }

    /**
     * 获取函数名，以(结尾
     *
     * @param content 内容
     * @return 函数名列表
     */
    public static List<String> fetchFun(String content) {
        return fetchFunQuote(content).stream().map(v -> v.replace("(", "")).collect(Collectors.toList());
    }

    /**
     * 获取函数名，以(结尾
     *
     * @param content 内容
     * @return 函数名列表
     */
    public static List<String> fetchFunQuote(String content) {
        List<String> matches = fetchVariable(content, FUN_PATTERN_ONE);
        matches.addAll(fetchVariable(content, FUN_PATTERN_TWO));
        return matches;
    }

    private static List<String> fetchVariable(String content, Pattern r) {
        List<String> result = new ArrayList<>();
        Matcher matcher = r.matcher(content);
        while (matcher.find()) {
            String m = matcher.group();
            result.add(m);
        }
        return result;
    }

    public static String ignoreString(String content) {
        return replaceAllString(content, IGNORE_PATTERN, "\"\"");
    }

    public static String ignoreString(String content, Map<String, String> replaceMap) {
        return replaceString(content, IGNORE_PATTERN, replaceMap);
    }

    private static String replaceString(String content, String reg, Map<String, String> replaceMap) {
        Pattern p = Pattern.compile(reg);
        return replaceString(content, p, replaceMap);
    }

    private static String replaceString(String content, Pattern p, Map<String, String> replaceMap) {
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            String m = matcher.group();
            String replacement = "{" + UUID.randomUUID().toString() + "}";
            replaceMap.put(replacement, m);
            content = content.replace(m, replacement);
        }
        return content;
    }

    private static String replaceAllString(String content, String reg, String replacement) {
        Pattern p = Pattern.compile(reg);
        return replaceAllString(content, p, replacement);
    }

    private static String replaceAllString(String content, Pattern p, String replacement) {
        Matcher m = p.matcher(content);
        return m.replaceAll(replacement);
    }

    public static String replaceLogical(String content) {
        return content.replace(" AND ", " && ").replace(" OR ", " || ");
    }

}
